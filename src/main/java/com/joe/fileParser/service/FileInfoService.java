package com.joe.fileParser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ZipUtil;
import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.config.ConstantConfig;
import com.joe.fileParser.parser.TikaParser;
import com.joe.fileParser.enumeration.ResponseMessage;
import com.joe.fileParser.model.FileInfo;
import com.joe.fileParser.model.FileInfoEs;
import com.joe.fileParser.model.TikaModel;
import com.joe.fileParser.repository.BaseRepository;
import com.joe.fileParser.repository.FileInfoEsRepository;
import com.joe.fileParser.repository.FileInfoRepository;
import com.joe.fileParser.repository.GridFSRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class FileInfoService extends BaseService<FileInfo, String> {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private GridFSRepository gridFSRepository;

    @Resource
    private TikaParser tikaParser;

    @Resource
    private FileInfoEsRepository fileInfoEsRepository;

    private static final Logger logger = LoggerFactory.getLogger(FileInfoService.class);

    @Override
    public BaseRepository<FileInfo, String> getRepository() {
        return this.fileInfoRepository;
    }

    public ResponseResult uploadFile(MultipartFile file) {
        if (file == null || file.getSize() == 0)
            return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_EMPTY);

        try (InputStream inputStream_parse = file.getInputStream();
             InputStream inputStream_gridfs = file.getInputStream()) {
            // 解析文件
            TikaModel tikaModel = this.tikaParser.parse(inputStream_parse, true);
            // 文件名称
            String originalFilename = file.getOriginalFilename();
            // 保存文件到GridFS
            ObjectId objectId = this.gridFSRepository.store(inputStream_gridfs, originalFilename, new Document(tikaModel.getMetaData()));
            if (objectId == null)
                return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_FAIL);
            // 文件扩展名 统一采用小写
            String extName = StringUtils.isNotBlank(originalFilename) ? FileUtil.extName(originalFilename).toLowerCase() : null;
            // 文件大小
            long size = file.getSize();
            // content-type
            String contentType = file.getContentType();
            // 保存文件信息 文件名称，大小等， 这里指定了文件的id为GridFS中文件的id，这样方便文件的查找
            FileInfo fileInfo = new FileInfo(objectId.toString(), originalFilename, extName, size, contentType, objectId.toString(), DateUtil.now());
            FileInfo insert = this.getRepository().insert(fileInfo);
            if (insert == null)
                throw new RuntimeException("file info save mongodb failed !");

            // 保存文件解析内容和名称到elastic search，这里也指定id为GridFS中文件的id
            FileInfoEs fileInfoEs = new FileInfoEs(objectId.toString(), originalFilename, tikaModel.getContent());
            String esId = this.fileInfoEsRepository.insert(fileInfoEs);
            if (StringUtils.isBlank(esId))
                throw new RuntimeException("file info save elastic search failed !");

            return ResponseResult.success().msg(ResponseMessage.UPLOAD_FILE_SUCCESS).data(insert);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
            return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_FAIL);
        }
    }

    /**
     * delete file info
     * 1.delete data of mongodb
     * 2.delete fail of GridFS
     * 3.delete data of elastic search
     *
     * @param ids file info id, file GridFS id, file info elastic search id
     * @return delete result and msg eg.
     */
    public ResponseResult deleteFileByIds(String[] ids) {
        try {
            this.fileInfoRepository.deleteByIds(ids);
            this.gridFSRepository.deleteByIds(ids);
            this.fileInfoEsRepository.deleteByIds(ids);
            return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }

    /**
     * get file content from elastic search to preview
     *
     * @param fileInfoId 文件id（fileInfo,gridFs,elastic search ）
     * @param response   httpServerResponse
     */
    public void previewFileContent(String fileInfoId, HttpServletResponse response) {
//        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try (final ServletOutputStream outputStream = response.getOutputStream()) {
            if (StringUtils.isBlank(fileInfoId)) {
                IoUtil.writeObjects(outputStream, false, "Invalid parameter. ");
            } else {
                final FileInfo byId = this.fileInfoRepository.findById(fileInfoId);
                final String contentType = byId.getContentType();
                if (contentType.contains("image") || contentType.contains("video") || contentType.contains("pdf")) {
                    final InputStream download = this.gridFSRepository.download(fileInfoId);
                    IoUtil.copy(download, outputStream);
                } else {
                    final FileInfoEs byDocumentId = this.fileInfoEsRepository.findByDocumentId(fileInfoId);
                    final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(byDocumentId.getContent());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
              if (bufferedInputStream != null) bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNeedReadSourceFile(String contentType) {
        for (String type : Arrays.asList("video", "image", "pdf", "audio")) {
            if (contentType.contains(type)) return true;
        }
        return false;
    }

    /**
     * search file content and name
     *
     * @param fileInfoEs search param
     * @return search result
     */
    public ResponseResult searchFile(FileInfoEs fileInfoEs) {
        try {
            final List<FileInfoEs> search = this.fileInfoEsRepository.search(fileInfoEs.getKeyword(), fileInfoEs.getPage(), fileInfoEs.getLimit());
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(search);
        } catch (Exception e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }

    /**
     * download file by single id
     *
     * @param fileInfoId file info id
     * @param response   http servlet response
     */
    public void downloadFile(String fileInfoId, HttpServletResponse response) {
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(this.gridFSRepository.download(fileInfoId));
             final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())
        ) {
            final FileInfo fileInfo = this.fileInfoRepository.findById(fileInfoId);
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileInfo.getName(), "UTF-8"));// 设置文件名
            IoUtil.copy(bufferedInputStream, bufferedOutputStream);
            if (logger.isInfoEnabled())
                logger.info("download file success");
        } catch (IOException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error("download file fail");
        }
    }

    public void downloadCompressFile(String downloadId, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        File file = null;
        try (final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())) {
            String fileName = downloadId + ".zip";
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));// 设置文件名
            file = FileUtil.file(ConstantConfig.SYSTEM_TEMP_PATH + File.separator + fileName);
            if (!file.exists())
                throw new RuntimeException("can not find dest file.");

            fileInputStream = new FileInputStream(file);
            IoUtil.copy(fileInputStream, bufferedOutputStream);
            if (logger.isInfoEnabled())
                logger.info("download file success");
        } catch (IOException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error("download file fail");
        } finally {
            IoUtil.close(fileInputStream);
            if (file != null && file.exists())
                FileUtil.del(file);
        }
    }

    public ResponseResult compressFile(String[] ids) {
        final List<File> files = this.getFiles(ids);
        if (files.isEmpty())
            return ResponseResult.fail().msg("compress file fail.");
        try {
            final String uuid = UUID.fastUUID().toString();
            final File file = FileUtil.file(ConstantConfig.SYSTEM_TEMP_PATH + File.separator + uuid + ".zip");
            final File zip = ZipUtil.zip(file, false, files.toArray(new File[]{}));
            if (zip == null || !zip.exists())
                return ResponseResult.fail().msg("compress file fail.");
            else
                return ResponseResult.success().msg("compress file success.").data(uuid);
        } catch (UtilException e) {
            e.printStackTrace();
        } finally {
            if (!files.isEmpty()) {
                FileUtil.del(files.get(0));
            }
        }
        return ResponseResult.fail().msg("compress file fail.");
    }

    private List<File> getFiles(String[] fileInfoIds) {
        try {
            if (ArrayUtil.isEmpty(fileInfoIds)) return Collections.emptyList();
            // get system temp dir
            final String tmpdir = ConstantConfig.SYSTEM_TEMP_PATH;
            StringBuilder stringBuilder = new StringBuilder(tmpdir)
                    .append(File.separator)
                    .append(UUID.fastUUID())
                    .append(File.separator);
            final List<GridFsResource> download = this.gridFSRepository.download(fileInfoIds);
            final List<File> files = new ArrayList<>();
            for (GridFsResource gridFsResource : download) {
                final InputStream inputStream = gridFsResource.getInputStream();
                final File file = FileUtil.writeFromStream(inputStream, stringBuilder + gridFsResource.getFilename());
                inputStream.close();
                files.add(file);
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
