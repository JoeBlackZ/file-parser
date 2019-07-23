package com.joe.fileParser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.joe.fileParser.common.ResponseResult;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
     * @param ids file info id, file GridFS id, file info elastic search id
     * @return delete result and msg eg.
     */
    public ResponseResult deleteFileByIds(String[] ids) {
        try {
            this.fileInfoRepository.deleteByIds(ids);
            this.gridFSRepository.deleteByIds(ids);
            this.fileInfoEsRepository.deleteByIds(ids);
            return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }

    /**
     * get file content from elastic search to preview
     * @param fileInfoId 文件id（fileInfo,gridFs,elastic search ）
     * @param response httpServerResponse
     */
    public void previewFileContent(String fileInfoId, HttpServletResponse response) {
//        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()){
            if (StringUtils.isBlank(fileInfoId)) {
                writer.write("Invalid parameter. ");
            } else {
                final FileInfoEs byDocumentId = this.fileInfoEsRepository.findByDocumentId(fileInfoId);
                writer.write(byDocumentId.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * search file content and name by keyword
     * include page info
     * @param fileInfoEs search params
     * @return return search result
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

}
