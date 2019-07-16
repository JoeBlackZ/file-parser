package com.joe.fileParser.service;

import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.enumeration.ResponseMessage;
import com.joe.fileParser.model.BaseModel;
import com.joe.fileParser.repository.BaseRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public abstract class BaseService<T extends BaseModel, ID extends Serializable> {

    public abstract BaseRepository<T, ID> getRepository();

    public ResponseResult insert(T t) {
        try {
            T insert = this.getRepository().insert(t);
            if (insert != null && insert.getId() != null) {
                return ResponseResult.success().msg(ResponseMessage.SAVE_SUCCESS).data(insert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.SAVE_FAIL);
    }

    public ResponseResult insertAll(Collection<T> collection) {
        try {
            Collection<T> results = this.getRepository().insertAll(collection);
            if (!results.isEmpty())
                return ResponseResult.success().msg(ResponseMessage.SAVE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.SAVE_FAIL);
    }


    public ResponseResult updateById(BaseModel baseModel) {
        try {
            long l = this.getRepository().updateById(baseModel);
            if (l > 0)
                return ResponseResult.success().msg(ResponseMessage.UPDATE_SUCCESS).data(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.UPDATE_FAIL);
    }


    public ResponseResult deleteByIds(Collection<? extends Serializable> collections) {
        return getResponseResult(this.getRepository().deleteByIds(collections));
    }


    public ResponseResult deleteByIds(ID[] ids) {
        return getResponseResult(this.getRepository().deleteByIds(ids));
    }

    private ResponseResult getResponseResult(long l) {
        try {
            if (l > 0)
                return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS).data(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }

    public ResponseResult deleteById(ID id) {
        try {
            long l = this.getRepository().deleteById(id);
            if (l > 0)
                return ResponseResult.success().msg(ResponseMessage.DELETE_SUCCESS).data(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.DELETE_FAIL);
    }


    public ResponseResult findAll() {
        try {
            List<T> all = this.getRepository().findAll();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(all);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findAllByPage(int pageNum, int pageSize) {
        try {
            List<T> allByPage = this.getRepository().findAllByPage(pageNum, pageSize);
            long count = this.getRepository().count();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(allByPage).count(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult find(BaseModel baseModel) {
        try {
            List<T> all = this.getRepository().find(baseModel);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(all);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findByPage(BaseModel baseModel) {
        try {
            if (baseModel == null)
                return this.findAll();
            List<T> byPage = this.getRepository().findByPage(baseModel);
            long count = this.getRepository().count(baseModel);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(byPage).count(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findOne(BaseModel baseModel) {
        try {
            T one = this.getRepository().findOne(baseModel);
            if (one != null)
                return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(one);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult findById(ID id) {
        try {
            T byId = this.getRepository().findById(id);
            if (byId != null)
                return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(byId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult count() {
        try {
            long count = this.getRepository().count();
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }


    public ResponseResult count(BaseModel baseModel) {
        try {
            long count = this.getRepository().count(baseModel);
            return ResponseResult.success().msg(ResponseMessage.QUERY_SUCCESS).data(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail().msg(ResponseMessage.QUERY_FAIL);
    }
}
