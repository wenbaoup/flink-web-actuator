package com.yijiupi.flink.actuator.controller;

import com.google.common.collect.Lists;
import com.yijiupi.flink.actuator.entity.*;
import com.yijiupi.flink.actuator.service.ITableParamsService;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.apache.calcite.sql.SqlKind.AS;
import static org.apache.calcite.sql.SqlKind.IDENTIFIER;

@Controller
public class TableParamsController {
    @Autowired
    ITableParamsService iTableParamsService;

    /**
     * 模拟数据库中的字段和值
     */
    private static Map<String, Map<String, String>> tableMap = new HashMap<>();

    private String sinkTableName;

    static {
        Map<String, String> loginLogMap = new HashMap<>();
        Map<String, String> bindRelationShipMap = new HashMap<>();
        tableMap.put("loginlog", loginLogMap);
        tableMap.put("bindrelationship", bindRelationShipMap);
        loginLogMap.put("id", "varchar");
        loginLogMap.put("tokenid", "varchar");
        loginLogMap.put("userid", "varchar");
        loginLogMap.put("usertype", "varchar");
        loginLogMap.put("deviceid", "varchar");
        loginLogMap.put("deviceos", "varchar");
        loginLogMap.put("devicetype", "int");
        loginLogMap.put("appcode", "varchar");
        loginLogMap.put("appversion", "varchar");
        loginLogMap.put("ip", "varchar");
        loginLogMap.put("logintime", "bigint");

        bindRelationShipMap.put("sourceuserid", "varchar");
        bindRelationShipMap.put("sourceusertype", "varchar");
        bindRelationShipMap.put("targetuserid", "varchar");
        bindRelationShipMap.put("targetusertype", "varchar");
        bindRelationShipMap.put("deleted", "boolean");


        Map<String, String> bizuser_realtime_hh = new HashMap<>();
        Map<String, String> city = new HashMap<>();
        Map<String, String> etl_categorygroup = new HashMap<>();
        Map<String, String> salemode = new HashMap<>();
        tableMap.put("bizuser_realtime_hh", bizuser_realtime_hh);
        tableMap.put("city", city);
        tableMap.put("etl_categorygroup", etl_categorygroup);
        tableMap.put("salemode", salemode);

        bizuser_realtime_hh.put("periodid", "int");
        bizuser_realtime_hh.put("cityid", "int");
        bizuser_realtime_hh.put("firstcategoryid", "bigint");
        bizuser_realtime_hh.put("itemproductsalemode", "int");
        bizuser_realtime_hh.put("type", "int");
        bizuser_realtime_hh.put("amount", "double");
        bizuser_realtime_hh.put("payamount", "double");
        bizuser_realtime_hh.put("ratioamount", "double");
        bizuser_realtime_hh.put("ratiopayamount", "double");
        bizuser_realtime_hh.put("statisticspackagecount", "double");

        city.put("id", "int");
        city.put("mode", "int");

        etl_categorygroup.put("categoryid", "bigint");
        etl_categorygroup.put("productcategorygroupmanagementid", "int");
        etl_categorygroup.put("groupid", "bigint");

        salemode.put("salemodeid", "int");
        salemode.put("isvalid", "int");
        salemode.put("salemodecategoryid", "int");

    }

    private String sourceTable = "loginlog";

    private String sql1 = "select\n" +
            "\t  llw.id\n" +
            "\t ,llw.appcode\n" +
            "\t ,llw.appversion\n" +
            "\t ,llw.deviceid\n" +
            "\t ,llw.deviceos\n" +
            "\t ,llw.devicetype\n" +
            "\t ,llw.ip\n" +
            "\t ,llw.logintime\n" +
            "\t ,llw.tokenid\n" +
            "\t ,br.targetuserid as userid\n" +
            "\t ,llw.usertype\n" +
            "\t ,'ods_trd_loginlog'  as tablename \n" +
            "\tfrom loginlog  llw\n" +
            "\tleft   join   bindrelationship br on   llw.userid = br.sourceuserid \n" +
            "\twhere br.sourceusertype = 'WeChatUser' and br.targetusertype = 'MallBizuser' and br.deleted is null and llw.usertype = 'WeChatUser'";


    private String sql2 = "select\n" +
            "\t  \tid\n" +
            "\t   ,tokenid\n" +
            "\t   ,userid\n" +
            "\t   ,usertype\n" +
            "\t   ,deviceid\n" +
            "\t   ,deviceos\n" +
            "\t   ,devicetype\n" +
            "\t   ,appcode\n" +
            "\t   ,appversion\n" +
            "\t   ,ip\n" +
            "\t   ,logintime\n" +
            "\t   ,'ods_trd_loginlog'  as tablename \n" +
            "from loginlog  where usertype = 'MallBizuser'";


    private String sql3 = "  select \n" +
            "\t\t b.periodid\n" +
            "\t\t,gp.groupid    as  categorygroupid\n" +
            "\t\t,s.salemodecategoryid  as salemodecategroyid\n" +
            "\t\t,c.mode        as  citymodeid\n" +
            "\t\t,b.type         as typeid\n" +
            "\t\t,sum(b.amount) as amount\n" +
            "\t\t,sum(b.payamount) as payamount\n" +
            "\t\t,sum(b.ratioamount) as ratioamount\n" +
            "\t\t,sum(b.ratiopayamount) as ratiopayamount\n" +
            "\t\t,sum(b.statisticspackagecount) as statisticspackagecount\n" +
            "        ,'dm_order_categorysalemode_realtime_hh' as tablename\n" +
            "\tfrom bizuser_realtime_hh b  \n" +
            "\tleft join city c  on b.cityid=c.id \n" +
            "\tleft join etl_categorygroup gp on gp.categoryid=b.firstcategoryid\n" +
            "\tleft join salemode s  on s.salemodeid=b.itemproductsalemode\n" +
            "\twhere gp.productcategorygroupmanagementid=1 and  s.isvalid=1\n" +
            "\tgroup by b.periodid ,c.mode,gp.groupid,s.salemodecategoryid,b.type";

    private String sql4 = "select\n" +
            "\t  \tlogintime   as  periodid\n" +
            "\t   ,logintime     as  periodhour\n" +
            "\t   ,count(distinct userid)   as usercount\n" +
            "\t   ,'dm_login_bizuser_realtime_hh'  as tablename \n" +
            "from ods_trd_loginlog \n";

    private String sql5 = "select\n" +
            "\t  \tlongToDate(logintime,'yyyyMMdd') as  periodid\n" +
            "\t   ,longToDate(logintime,'HH')       as  periodhour\n" +
            "\t   ,count(distinct userid)   as usercount\n" +
            "\t   ,'dm_login_bizuser_realtime_hh'  as tablename \n" +
            "from ods_trd_loginlog \n" +
            "group by longToDate(logintime,'yyyyMMdd') ,longToDate(logintime,'HH')\n";

    @ResponseBody
    @RequestMapping("/queryTableParams")
    public void userList() throws SqlParseException, IOException {
        //todo  通过source name查询TableParams表 然后判断该表是否为主表 如果是维表则报错
        sql1 = sql1.replaceAll("--.*", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\n", " ")
                .replace("\t", " ").trim();
        Map<String, ColumnInfo> selectFieldMapper = new LinkedHashMap<>();

        SqlParser.Config config = SqlParser
                .configBuilder()
                //从mysql改为MYSQL_ANSI
                .setLex(Lex.MYSQL_ANSI)
                .build();
        SqlParser sqlParser = SqlParser.create(sql3, config);
        SqlNode sqlNode = null;
        sqlNode = sqlParser.parseStmt();
        //目前不支持子查询
        SqlParseResult sqlParseResult = new SqlParseResult();
        //将targetTable  MyResult  sourceTable MyTable,sideTable 解析出来放入sqlParseResult
        parseTableNode(sqlNode, sqlParseResult);
        //将sql中所有用到的字段建立map映射 tableName——> columnName
        parseColumnNode(sqlNode, selectFieldMapper);
        //将查询的sql保存
        sqlParseResult.setExecSql(sql3);
        //tableList 中是tableName和别名  如果只有一张表不需要别名 单表必须保证where条件中的字段在select中
        if (sqlParseResult.tableList.size() == 1) {
            for (ColumnInfo columnInfo : selectFieldMapper.values()) {
                //将表名设置到列中 将列设置到表中
                if ("tablename".equalsIgnoreCase(columnInfo.getSinkColumnName())) {
                    continue;
                }
                columnInfo.setTableName(sqlParseResult.tableList.get(0).getTableName());
                sqlParseResult.tableList.get(0).getColumnInfo().add(columnInfo);
            }
        } else {
            for (TableInfo tableInfo : sqlParseResult.tableList) {
                for (ColumnInfo columnInfo : selectFieldMapper.values()) {
                    if (columnInfo.getTableAlias().equalsIgnoreCase(tableInfo.getTableAlias())) {
                        //如果两个别名相同则证明是同一张表
                        //将表名设置到列中 将列设置到表中
                        columnInfo.setTableName(tableInfo.getTableName());
                        tableInfo.getColumnInfo().add(columnInfo);
                    }
                }
                System.out.println(tableInfo.getTableAlias());
            }
        }

        //todo 查询所有的字段类型 如果查询是维表则需要判断是否查询主键字段  目前支持单主键
        List<ColumnInfo> columnInfos = new ArrayList<>();
        // 如果有查询字段未在数据库中查询到  则直接报错提醒 通过表名和字段名查询数据库中对应的字段类型
        for (ColumnInfo columnInfo : selectFieldMapper.values()) {
            if (null != tableMap.get(columnInfo.getTableName())) {
                //这里应该查询数据库 demo方便直接写成静态变量
                String columnType = tableMap.get(columnInfo.getTableName()).get(columnInfo.getSourceColumnName());
                columnInfo.setSourceColumnType(columnType);
                columnInfo.setSinkColumnType(columnType);
            }
            columnInfos.add(columnInfo);
        }
        //todo 填充sink需要的表名 字段名和字段类型
        //设置Sink对象
        TableSinkInfo tableSinkInfo = new TableSinkInfo();
        //字段有两种属性 source和sink  字段只是属于其中一个
        for (ColumnInfo columnInfo : columnInfos) {
            if ("tablename".equalsIgnoreCase(columnInfo.getSinkColumnName())) {
                columnInfo.setSinkColumnType("varchar");
                sinkTableName = columnInfo.getTableAlias();
            }
            //todo  根据函数名称解析对应的返回值 作为sink column的columnType
            if (AggFunType.SUM.equals(columnInfo.getAggFunType())) {
                columnInfo.setSinkColumnType("double");
            }
            if (AggFunType.COUNT.equals(columnInfo.getAggFunType())) {
                columnInfo.setSinkColumnType("bigint");
            }
            if (AggFunType.UDF.equals(columnInfo.getAggFunType())) {
                //todo 通过udfName查询数据库得到返回类型
                columnInfo.setSinkColumnType("varchar");
            }
            if (null != columnInfo.getSinkColumnName()) {
                tableSinkInfo.getList().add(columnInfo);
            }
        }
        tableSinkInfo.setTableName(sinkTableName);
        //利用现有的字段和类型写文件 文件名称为任务名称
        writerFile(selectFieldMapper, tableSinkInfo, "test", sqlParseResult);
        System.out.println("填充完成");
    }

    private void writerFile(Map<String, ColumnInfo> selectFieldMapper, TableSinkInfo tableSinkInfo, String fileName, SqlParseResult sqlParseResult) throws IOException {
        File file = new File("test.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        //使用true，即进行append file

        FileWriter fileWriter = new FileWriter(file.getName(), true);

        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

        String content = buildContent(selectFieldMapper, tableSinkInfo, sqlParseResult);
        bufferWriter.write(content);

        bufferWriter.close();

        System.out.println("finish");
    }

    private String buildContent(Map<String, ColumnInfo> selectFieldMapper, TableSinkInfo tableSinkInfo, SqlParseResult sqlParseResult) {
        StringBuilder stringBuffer = new StringBuilder();
        //循环遍历所有的表(不包含sink表)
        for (TableInfo tableInfo : sqlParseResult.tableList) {
            //利用LinkedHashMap去重和保证原有顺序  去掉sourceColumnName和TableName相同的字段
            Map<ColumnInfo, Integer> sourceColumnMap = new LinkedHashMap<>();
            for (ColumnInfo columnInfo : selectFieldMapper.values()) {
                if (tableInfo.getTableName().equalsIgnoreCase(columnInfo.getTableName()) && null != columnInfo.getSourceColumnName()) {
                    sourceColumnMap.put(columnInfo, 1);
                }
            }
            String primaryKey = null;
            stringBuffer.append("CREATE TABLE  ").append(tableInfo.getTableName()).append(" ( \n");
            for (ColumnInfo columnInfo : sourceColumnMap.keySet()) {
                //只有维表才关注primaryKey 只有on后的字段才赋值
                if (null != columnInfo.getIsSidePrimary()) {
                    primaryKey = columnInfo.getSourceColumnName();
                }
                stringBuffer.append("\t").append(columnInfo.getSourceColumnName()).append("  ")
                        .append(columnInfo.getSourceColumnType()).append(" ,").append("\n");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 2);

            //如果不等于源表则证明都是维表
            if (!sourceTable.equalsIgnoreCase(tableInfo.getTableName())) {
                stringBuffer.append("\t").append(",PRIMARY KEY(").append(primaryKey).append(")\n")
                        .append("\t").append(",PERIOD FOR SYSTEM_TIME \n");
            }
            stringBuffer.append(")WITH( ").append("\n").append(");").append("\n");
        }

        stringBuffer.append("CREATE TABLE ").append(tableSinkInfo.getTableName()).append(" (\n");
        for (ColumnInfo columnInfo : tableSinkInfo.getList()) {
            stringBuffer.append("\t").append(columnInfo.getSinkColumnName()).append("  ")
                    .append(columnInfo.getSinkColumnType()).append(" ,").append("\n");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 2);
        stringBuffer.append(")WITH( ").append("\n").append(");").append("\n");
        stringBuffer.append("insert into ").append(tableSinkInfo.getTableName()).append("\n")
                .append(sqlParseResult.getExecSql()).append(";");
        return stringBuffer.toString();
    }

    /**
     * 只解析tableName以及映射
     *
     * @param sqlNode
     * @param sqlParseResult
     */
    private static void parseTableNode(SqlNode sqlNode, SqlParseResult sqlParseResult) {
        SqlKind sqlKind = sqlNode.getKind();
        TableInfo tableInfo = new TableInfo();
        switch (sqlKind) {
            case INSERT:
                SqlNode sqlTarget = ((SqlInsert) sqlNode).getTargetTable();
                SqlNode sqlSource = ((SqlInsert) sqlNode).getSource();
                tableInfo.setTableName(sqlTarget.toString());
                sqlParseResult.addTable(tableInfo);
                parseTableNode(sqlSource, sqlParseResult);
                break;
            case SELECT:
                SqlNode sqlFrom = ((SqlSelect) sqlNode).getFrom();
                if (sqlFrom.getKind() == IDENTIFIER) {
                    tableInfo.setTableName(sqlFrom.toString());
                    sqlParseResult.addTable(tableInfo);
                } else {
                    parseTableNode(sqlFrom, sqlParseResult);
                }
                break;
            case JOIN:
                SqlNode leftNode = ((SqlJoin) sqlNode).getLeft();
                SqlNode rightNode = ((SqlJoin) sqlNode).getRight();

                if (leftNode.getKind() == IDENTIFIER) {
                    tableInfo.setTableName(leftNode.toString());
                    sqlParseResult.addTable(tableInfo);
                } else {
                    parseTableNode(leftNode, sqlParseResult);
                }
                if (rightNode.getKind() == IDENTIFIER) {
                    tableInfo.setTableName(rightNode.toString());
                    sqlParseResult.addTable(tableInfo);
                } else {
                    parseTableNode(rightNode, sqlParseResult);
                }
                break;
            case AS:
                //不解析column,所以 as 相关的都是表
                SqlNode identifierNode = ((SqlBasicCall) sqlNode).getOperands()[0];
                if (identifierNode.getKind() != IDENTIFIER) {
                    parseTableNode(identifierNode, sqlParseResult);
                } else {
                    SqlNode alias = ((SqlBasicCall) sqlNode).getOperands()[1];
                    tableInfo.setTableName(identifierNode.toString());
                    tableInfo.setTableAlias(alias.toString());
                    sqlParseResult.addTable(tableInfo);
                }
                break;
            case UNION:
                SqlNode unionLeft = ((SqlBasicCall) sqlNode).getOperands()[0];
                SqlNode unionRight = ((SqlBasicCall) sqlNode).getOperands()[1];
                if (unionLeft.getKind() == IDENTIFIER) {
                    tableInfo.setTableName(unionLeft.toString());
                    sqlParseResult.addTable(tableInfo);
                } else {
                    parseTableNode(unionLeft, sqlParseResult);
                }
                if (unionRight.getKind() == IDENTIFIER) {
                    tableInfo.setTableName(unionRight.toString());
                    sqlParseResult.addTable(tableInfo);
                } else {
                    parseTableNode(unionRight, sqlParseResult);
                }
                break;
            default:
                //do nothing
                break;
        }
    }

    private void parseColumnNode(SqlNode sqlNode, Map<String, ColumnInfo> selectFieldMapper) {
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case INSERT:
                SqlNode sqlSource = ((SqlInsert) sqlNode).getSource();
                parseColumnNode(sqlSource, selectFieldMapper);
                break;
            case SELECT:
                for (SqlNode sqlNode1 : ((SqlSelect) sqlNode).getSelectList().getList()) {
                    ColumnInfo columnInfo = new ColumnInfo();
                    String fieldName = sqlNode1.toString();
                    //判断是否有as
                    if (sqlNode1.getKind() == AS) {
                        //判断是否有函数包括自定义函数   tablename直接当做特殊的字面量函数  sum(b.payamount) as payamount
                        if (((SqlBasicCall) sqlNode1).getOperands()[0].getKind() != IDENTIFIER) {
                            parseAggFunNode(((SqlBasicCall) sqlNode1).getOperands()[0], columnInfo);
                            //判断是否有table别名   gp.groupid    as  categorygroupid
                        } else if (((SqlBasicCall) sqlNode1).getOperands()[0].toString().contains(".")) {
                            columnInfo.setTableAlias(((SqlBasicCall) sqlNode1).getOperands()[0].toString().split("\\.")[0].replaceAll("\'", "").trim());
                            columnInfo.setSourceColumnName(((SqlBasicCall) sqlNode1).getOperands()[0].toString().split("\\.")[1].replaceAll("\'", "").trim());
                        } else {
                            //没有别名 logintime   as  periodid
                            columnInfo.setSourceColumnName(((SqlBasicCall) sqlNode1).getOperands()[0].toString());
                        }
                        columnInfo.setSinkColumnName(((SqlBasicCall) sqlNode1).getOperands()[1].toString());
                    } else if (sqlNode1.toString().contains(".")) {
                        //是否携带别名 b.periodid
                        columnInfo.setSinkColumnName(fieldName.split("\\.")[1]);
                        columnInfo.setSourceColumnName(fieldName.split("\\.")[1]);
                        columnInfo.setTableAlias(fieldName.split("\\.")[0]);
                    } else {
                        // 没有别名 如sql2 select tokenid
                        columnInfo.setSinkColumnName(fieldName);
                        columnInfo.setSourceColumnName(fieldName);
                    }
                    selectFieldMapper.put(fieldName, columnInfo);
                }
                SqlNode sqlFrom = ((SqlSelect) sqlNode).getFrom();
                //解析where 后的字段  select中可能不包含这些字段
                parseWhereNode(((SqlSelect) sqlNode).getWhere(), selectFieldMapper);
                if (sqlFrom.getKind() != IDENTIFIER) {
                    parseColumnNode(sqlFrom, selectFieldMapper);
                }
                break;
            case JOIN:
                //解析on 后的字段  select中可能不包含这些字段
                parseOnNode(sqlNode, selectFieldMapper);
                SqlNode leftNode = ((SqlJoin) sqlNode).getLeft();
                SqlNode rightNode = ((SqlJoin) sqlNode).getRight();

                if (leftNode.getKind() != IDENTIFIER) {
                    parseColumnNode(leftNode, selectFieldMapper);
                }

                if (rightNode.getKind() != IDENTIFIER) {
                    parseColumnNode(rightNode, selectFieldMapper);
                }
                break;
            case AS:
                //解析column
                SqlNode info = ((SqlBasicCall) sqlNode).getOperands()[0];
                if (info.getKind() != IDENTIFIER) {
                    parseColumnNode(info, selectFieldMapper);
                }
                break;
            case UNION:
                SqlNode unionLeft = ((SqlBasicCall) sqlNode).getOperands()[0];
                SqlNode unionRight = ((SqlBasicCall) sqlNode).getOperands()[1];
                if (unionLeft.getKind() != IDENTIFIER) {
                    parseColumnNode(unionLeft, selectFieldMapper);
                }
                if (unionRight.getKind() != IDENTIFIER) {
                    parseColumnNode(unionRight, selectFieldMapper);
                }
                break;
            default:
                //do nothing
                break;
        }
    }

    /**
     * 解析聚合函数和自定义函数
     * 自定义函数名称需要注册到枚举中或者用Map判断
     *
     * @param sqlNode
     * @param columnInfo
     */
    private void parseAggFunNode(SqlNode sqlNode, ColumnInfo columnInfo) {
        //通过on获取维表主键  然后打标  因为涉及到自定义函数问题 没有将sum和count的通用提取
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case SUM:
                String operandSumString = ((SqlBasicCall) sqlNode).getOperands()[0].toString();
                if (operandSumString.split("\\.").length == 1) {
                    columnInfo.setSourceColumnName(operandSumString);
                } else {
                    columnInfo.setSourceColumnName(operandSumString.split("\\.")[1]);
                    columnInfo.setTableAlias(operandSumString.split("\\.")[0]);
                }
                columnInfo.setAggFunType(AggFunType.SUM);
                break;
            case COUNT:
                String operandCountString = ((SqlBasicCall) sqlNode).getOperands()[0].toString();
                if (operandCountString.split("\\.").length == 1) {
                    columnInfo.setSourceColumnName(operandCountString);
                } else {
                    columnInfo.setSourceColumnName(operandCountString.split("\\.")[1]);
                    columnInfo.setTableAlias(operandCountString.split("\\.")[0]);
                }
                columnInfo.setAggFunType(AggFunType.COUNT);
                break;
            case LITERAL:
                //字面量   如tablename
                columnInfo.setTableAlias(sqlNode.toString().replaceAll("\'", "").trim());
                break;
            case OTHER_FUNCTION:
                //自定义函数
                String operandCustomString = ((SqlBasicCall) sqlNode).getOperands()[0].toString();
                if (operandCustomString.split("\\.").length == 1) {
                    columnInfo.setSourceColumnName(operandCustomString);
                } else {
                    columnInfo.setSourceColumnName(operandCustomString.split("\\.")[1]);
                    columnInfo.setTableAlias(operandCustomString.split("\\.")[0]);
                }
                columnInfo.setAggFunType(AggFunType.UDF);
                columnInfo.setUdfName(((SqlBasicCall) sqlNode).getOperator().toString());
                break;
            default:
                //do nothing
                break;
        }
    }


    private void parseOnNode(SqlNode sqlNode, Map<String, ColumnInfo> selectFieldMapper) {
        //通过on获取维表主键  然后打标
        System.out.println(sqlNode.toString());
        SqlNode joinNode = ((SqlJoin) sqlNode).getCondition();
        SqlKind sqlKind = joinNode.getKind();
        switch (sqlKind) {
            case EQUALS:
                //如果没有别名会直接抛弃该字段 默认是常量
                //在on条件中会用到select 字段例如 br.targetuserid as userid   on llw.userid = br.sourceuserid 用的是as后的字段
                //通过on条件反向生成维表主键
                for (int i = 0; i < ((SqlBasicCall) joinNode).getOperands().length; i++) {
                    String operandString = ((SqlBasicCall) joinNode).getOperands()[i].toString();
                    setOnField(selectFieldMapper, operandString);
                }
                break;
            default:
                //do nothing
                break;
        }
    }

    private void parseWhereNode(SqlNode sqlNode, Map<String, ColumnInfo> selectFieldMapper) {
        if (null == sqlNode) {
            return;
        }
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case AND:
                if (((SqlBasicCall) sqlNode).getOperands().length > 1) {
                    parseWhereNode(((SqlBasicCall) sqlNode).getOperands()[1], selectFieldMapper);
                }
                parseWhereNode(((SqlBasicCall) sqlNode).getOperands()[0], selectFieldMapper);
                break;
            case OR:
                break;
            case EQUALS:
                //如果没有别名会直接抛弃该字段 默认是常量
                for (int i = 0; i < ((SqlBasicCall) sqlNode).getOperands().length; i++) {
                    String operandString = ((SqlBasicCall) sqlNode).getOperands()[i].toString();
                    setWhereField(selectFieldMapper, operandString);
                }
                break;
            case IS_NULL:
                String operandString = ((SqlBasicCall) sqlNode).getOperands()[0].toString();
                setWhereField(selectFieldMapper, operandString);
                break;
            default:
                //do nothing
                break;
        }
        System.out.println(sqlNode.toString());
    }

    private void setWhereField(Map<String, ColumnInfo> selectFieldMapper, String operandString) {
        if (operandString.split("\\.").length > 1) {
            if (null == selectFieldMapper.get(operandString)) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setSourceColumnName(operandString.split("\\.")[1]);
                columnInfo.setTableAlias(operandString.split("\\.")[0]);
                selectFieldMapper.put(operandString, columnInfo);
            }
        }
    }

    private void setOnField(Map<String, ColumnInfo> selectFieldMapper, String operandString) {
        if (operandString.split("\\.").length > 1) {
            if (null == selectFieldMapper.get(operandString)) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setSourceColumnName(operandString.split("\\.")[1]);
                columnInfo.setTableAlias(operandString.split("\\.")[0]);
                columnInfo.setIsSidePrimary(true);
                selectFieldMapper.put(operandString, columnInfo);
            }
        }
    }


    public static class SqlParseResult {

        private List<TableInfo> tableList = Lists.newArrayList();


        private String execSql;

        public void addTable(TableInfo tableInfo) {
            tableList.add(tableInfo);
        }

        public List<TableInfo> getTableList() {
            return tableList;
        }

        public String getExecSql() {
            return execSql;
        }

        public void setExecSql(String execSql) {
            this.execSql = execSql;
        }
    }
}
