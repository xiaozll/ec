/**
*  Copyright (c) 江西省锦峰软件科技有限公司 2013-2020 http://www.jfit.com.cn
*
*/
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.MailContact;

import java.util.List;

/**
 * 邮件联系人
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2020-03-05
 */
@MyBatisDao
public interface MailContactDao extends CrudDao<MailContact> {


    List<MailContact> checkExist(Parameter parameter);

    List<MailContact> findByContactGroupId(Parameter parameter);

    List<MailContact> findByUserId(Parameter parameter);

    List<MailContact> findByUserIdWithInclude(Parameter parameter);

}
