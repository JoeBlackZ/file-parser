package com.joe.fileParser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.enumeration.ResponseMessage;
import com.joe.fileParser.model.FileInfo;
import com.joe.fileParser.repository.BaseRepository;
import com.joe.fileParser.repository.FileInfoRepository;
import com.joe.fileParser.repository.GridFSRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileInfoService extends BaseService<FileInfo, String> {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private GridFSRepository gridFSRepository;

    private static final Logger logger = LoggerFactory.getLogger(FileInfoService.class);

    @Override
    public BaseRepository<FileInfo, String> getRepository() {
        return this.fileInfoRepository;
    }

    public ResponseResult uploadFile(MultipartFile file) {
        if (file == null || file.getSize() == 0)
            return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_EMPTY);

        try (InputStream inputStream = file.getInputStream()){
            String originalFilename = file.getOriginalFilename();
            String extName = StringUtils.isNotBlank(originalFilename) ? FileUtil.extName(originalFilename).toLowerCase() : null;

            ObjectId objectId = this.gridFSRepository.store(inputStream, originalFilename);
            if (objectId == null)
                return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_FAIL);

            long size = file.getSize();
            String contentType = file.getContentType();

            FileInfo fileInfo = new FileInfo(originalFilename, extName, size, contentType, objectId.toString(), DateUtil.now());
            FileInfo insert = this.getRepository().insert(fileInfo);
            return ResponseResult.success().msg(ResponseMessage.UPLOAD_FILE_SUCCESS).data(insert);
        } catch (IOException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
            return ResponseResult.fail().msg(ResponseMessage.UPLOAD_FILE_FAIL);
        }


    }

}
