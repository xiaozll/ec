-- 4.1.20191115
ALTER TABLE `t_sys_serial_number`
ADD COLUMN `APP` varchar(36) COMMENT 'APP标识' ;

ALTER TABLE `t_sys_version_log`
ADD COLUMN `IS_SHELF` varchar(36) COMMENT 'APP标识' ;