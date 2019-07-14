package com.joe.fileParser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.config.TikaParser;
import com.joe.fileParser.enumeration.ResponseMessage;
import com.joe.fileParser.model.FileInfo;
import com.joe.fileParser.model.FileInfoEs;
import com.joe.fileParser.model.TikaModel;
import com.joe.fileParser.repository.BaseRepository;
import com.joe.fileParser.repository.FileInfoEsRepository;
import com.joe.fileParser.repository.FileInfoRepository;
import com.joe.fileParser.repository.GridFSRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

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

        try (InputStream inputStream = file.getInputStream()) {
            // 解析文件
            TikaModel tikaModel = this.tikaParser.parse2(inputStream);
            // 文件名称
            String originalFilename = file.getOriginalFilename();
            // 保存文件到GridFS
            ObjectId objectId = this.gridFSRepository.store(inputStream, originalFilename, new Document(tikaModel.getMetaData()));
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
        } catch (IOException | TikaException | RuntimeException | SAXException e) {
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
     * @param id file info id, file GridFS id, file info elastic search id
     * @return delete result and msg eg.
     */
    public ResponseResult deleteFileByIds(String[] id) {
        try {
            this.fileInfoRepository.deleteByIds(id);
            this.gridFSRepository.deleteByIds(id);
            this.fileInfoEsRepository.deleteByIds(id);
            return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }

}
