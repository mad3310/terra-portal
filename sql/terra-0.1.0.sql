-- 新增redis信息表
CREATE TABLE `TERRA_REDIS` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'redis信息表主键',
  `NAME` varchar(100) DEFAULT NULL COMMENT '名称',
  `TYPE` tinyint(2) DEFAULT NULL COMMENT '类型:2-Redis-Cluster,5-sentinel,6-standalone',
  `MEMORY_SIZE` int(10) DEFAULT NULL COMMENT '缓存大小',
  `REGION_ID` varchar(20) DEFAULT NULL COMMENT '地域ID',
  `CONFIG_ID` varchar(20) DEFAULT NULL COMMENT '配置文件ID',
  `AZ_ID` varchar(20) DEFAULT NULL COMMENT '可用区ID',
  `PASSWORD` varchar(32) DEFAULT NULL COMMENT '密码',
  `SERVICE_ID` varchar(20) DEFAULT NULL COMMENT '实例服务ID',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '创建人ID',
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '更新人ID',
  `AUDIT_INFO` varchar(200) DEFAULT NULL COMMENT '审核信息',
  `AUDIT_TIME` datetime DEFAULT NULL COMMENT '审核时间',
  `AUDIT_USER` bigint(20) unsigned DEFAULT NULL COMMENT '审核人',
  `STATUS` tinyint(4) DEFAULT NULL COMMENT '状态：1-待审核，2-审核通过，3-驳回，4-创建中，5-创建失败，6-启动中，7-启动失败，8-运行中，9-停止中，10-已停止，11-危险，12-宕机，13-集群删除中，14-集群已删除，15-集群删除失败',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='redis信息表'

-- 新增基础配额表
CREATE TABLE `TERRA_QUOTA_BASE` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '基础配额表主键',
  `NAME` varchar(100) DEFAULT NULL COMMENT '产品名称，例如REDIS，RDS',
  `TYPE` varchar(100) DEFAULT NULL COMMENT '类型，例如num，cpu',
  `VALUE` bigint(20) DEFAULT NULL COMMENT '值大小',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '创建人ID',
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基础配额表'

-- 新增用户配额表
CREATE TABLE `TERRA_QUOTA_USER` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户配额表主键',
  `USED` bigint(20) DEFAULT NULL COMMENT '使用量',
  `QUOTA_BASE_ID` bigint(20) DEFAULT NULL COMMENT '基础配置表ID',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '创建人ID',
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户配额表'

ALTER TABLE WEBPORTAL_TEMPLATE_TASK_CHAIN ADD COLUMN URL VARCHAR(200) COMMENT "调用地址";
ALTER TABLE WEBPORTAL_TASK_CHAIN ADD COLUMN URL VARCHAR(200) COMMENT "调用地址";
