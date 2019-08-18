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

    /**
     * 文件信息
     */
    @Resource
    private FileInfoRepository fileInfoRepository;

    /**
     * GridFS
     */
    @Resource
    private GridFSRepository gridFSRepository;

    /**
     * 解析器
     */
    @Resource
    private TikaParser tikaParser;

    /**
     * elastic search
     */
    @Resource
    private FileInfoEsRepository fileInfoEsRepository;

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FileInfoService.class);

    @Override
    public BaseRepository<FileInfo, String> getRepository() {
        return this.fileInfoRepository;
    }

    /**
     * 文件上传
     * @param file 要上传的文件
     * @return 返回文件上传结果
     */
    public ResponseResult uploadFile(MultipartFile file) {
        if (file == null || file.getSize() == 0)
            return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_EMPTY);

        try (InputStream inputStream_parse = file.getInputStream();
             InputStream inputStream_gridfs = file.getInputStream()) {
            // 解析文件
            TikaModel tikaModel = this.tikaParser.parse(inputStream_parse);
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
     * 批量删除文件 1.文件信息；2.源文件；3.解析的文件内容
     * @param ids 多个id
     * @return 返回删除结果
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
     * 预览解析后的内容，pdf、视频等文件预览源文件
     * @param fileInfoId 文件id（fileInfo,gridFs,elastic search ）
     * @param response 响应
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

    /**
     * 需要预览源文件的类型
     * @param contentType content-type
     * @return 是否预览源文件
     */
    private boolean isNeedReadSourceFile(String contentType) {
        for (String type : Arrays.asList("video", "image", "pdf", "audio")) {
            if (contentType.contains(type)) return true;
        }
        return false;
    }

    /**
     * 全文检索
     * @param fileInfoEs 检索参数
     * @return 匹配结果
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
     * 下载单个文件
     * @param fileInfoId 文件id
     * @param response 响应
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

    /**
     * 下载被压成压缩包的多个文件
     * @param downloadId 压缩文件名称
     * @param response 响应
     */
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

    /**
     * 下载多个文件，多个文件被压缩成压缩包
     * @param ids 多个文件id
     * @return 返回压缩结果
     */
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

    /**
     * 根据多个文件id，从GridFs下载文件
     * @param fileInfoIds 文件id
     * @return 返回文件列表
     */
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
