package com.bp.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;



/**
 * jdbc操作数据库
 * @author zhaor
 *
 */
public class JdbcUtil {
	
	/** mysql数据库连接URL*/  
    private final static String DB_URL = "jdbc:mysql://rdsmgm3k3u4yx3g8q278o.mysql.rds.aliyuncs.com:3306/yzt?useUnicode=true&characterEncoding=utf-8";  
      
    /** mysql数据库连接驱动*/  
    private final static String DB_DRIVER = "com.mysql.jdbc.Driver";  
      
    /** 数据库用户名*/  
    private final static String DB_USERNAME = "yztdbroot";  
      
    /** 数据库密码*/  
    private final static String DB_PASSWORD = "vv7NBbGUpImNsuVTA_"; 
    
    /** 
     * 获取数据库连接 
     * @return 
     */  
    public Connection getConnection(){  
        /** 声明Connection连接对象*/  
        Connection conn = null;  
        try{  
            /** 使用Class.forName()方法自动创建这个驱动程序的实例且自动调用DriverManager来注册它*/  
            Class.forName(DB_DRIVER);  
            /** 通过DriverManager的getConnection()方法获取数据库连接*/  
            conn = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return conn;  
    } 
    /** 
     * 关闭数据库连接 
     *  
     * @param connect 
     */  
    public void closeConnection(Connection conn){  
        try{  
            if(conn!=null){  
                /** 判断当前连接连接对象如果没有被关闭就调用关闭方法*/  
                if(!conn.isClosed()){  
                    conn.close();  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
    } 
    
    /**
     * 登录操作
     */
    public boolean login(String userName,String pwd)
    {
    	String sql="select * from SLOT_USER_INFO where USER_NAME='"+userName+"' and USER_PWD='"+pwd+"'";
    	Connection conn=getConnection();
    	PreparedStatement pstmt;
    	try {
    		pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
            	return true;
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
    	closeConnection(conn);
    	return false;
    }
    
    /**
     * 通用查询
     */
    
    
    /**
     * 通用更新
     * @param <T>
     * @param sql
     * @param objects
     */
    public Integer update(String sql,Object...objects) throws Exception
    {
    	int result=0;
    	Connection conn=getConnection();//获取连接
    	conn.setAutoCommit(true);
    	PreparedStatement ps = null; 
    	ps = conn.prepareStatement(sql);
    	
    	//赋值
    	if(objects!=null&&objects.length>0)
    	{
    		for(int i=0;i<objects.length;i++)
    		{
    			ps.setObject(i+1,objects[i]);
    		}
    	}
    	result=ps.executeUpdate();
    	ps.close();
    	closeConnection(conn);//关闭连接
    	return result;
    }
  
    /**
     * 拼写sql语句
     * @return
     */
    public static String setSqlStr()
    {
    	//此处默认还款标记1,即
    	StringBuffer sb = new StringBuffer();
		sb.append(" SELECT m.id as id, mbi.borrow_title,mbi.salesman,mri.principal, mri.current_period, mri.refund_date,");
		sb.append(" abi.global_status, mbi.cus_number,mbi.refund_way, mrti.begin_refund_total,");
		sb.append(" mrti.should_refund_total, mrti.begin_daihuan_refund_total, mrti.daihuan_refund_total, mbi.badRecord, mbi.responsible_uid, ");
		sb.append(" mri.begin_should_refund, ");
        sb.append(" hpi.NAME as product_name, m.uname, m.mobile_phone, m.source, mbi.source_remark, mra.real_name, mra.id_card, su.NAME AS user_name, mrl.final_urge_date, mrl.urge_count, mrl.urge_status, ");
	    
		sb.append("   ROUND(mri.principal,2) as principal,         ");
		sb.append("   ROUND(mri.interest, 2) as interest,       ");
		sb.append("   CONVERT(hpi.is_online, SIGNED) AS is_online,  ");
		sb.append("   IFNULL(SUBSTRING_INDEX(mri.current_period, '/', - 1) - SUBSTRING_INDEX(mri.current_period, '/', 1),0) as remain_num,  ");
		sb.append("  IFNULL(SUBSTRING_INDEX(mri.current_period, '/', - 1),0) AS mother, ");
		sb.append("  IFNULL(SUBSTRING_INDEX(mri.current_period, '/', 1),0) AS child, ");
		//逾期次数
		/*sb.append("  t.number1 as layz_number , ");*/
		//排序状态
		sb.append("  CASE                          ");
		sb.append("   WHEN abi.global_status = 19  ");
		sb.append("   THEN 1                       ");
		sb.append("   WHEN abi.global_status = 23  ");
		sb.append("   THEN 2                       ");
		sb.append("   WHEN abi.global_status = 37  ");
		sb.append("   THEN 3                       ");
		sb.append("   WHEN abi.global_status = 40  ");
		sb.append("   THEN 4                       ");
		sb.append("   WHEN abi.global_status = 29  ");
		sb.append("   THEN 5                       ");
		sb.append("   WHEN abi.global_status = 35  ");
		sb.append("   THEN 6                       ");
		sb.append("   ELSE 7                       ");
		sb.append(" END AS order_status ");


		sb.append(" from (select m_bid, global_status from audit_bid_info where (1)) abi ");
    	sb.append(" LEFT JOIN member_bid_info mbi ON mbi.id = abi.m_bid ");
    	sb.append(" LEFT JOIN (SELECT  temp.mb_id,COUNT(temp.overdue_day) AS number1   from member_refund_info temp  WHERE temp.overdue_day > 0  GROUP BY temp.mb_id) t ON t.mb_id=mbi.id") ;
    	sb.append(" LEFT JOIN (SELECT MIN(mri.id) AS id, mri.mb_id, mri.`status`, mri.refund_date, mri.overdue_day, IFNULL(mri.principal_interest,0) + IFNULL(mri.month_manage,0) + IFNULL(mri.overdue_manage,0) + ");
    	sb.append(" IFNULL(mri.overdue_fine,0) + IFNULL(mri.revenue,0) AS begin_should_refund, IFNULL(mri.principal_interest,0) + IFNULL(mri.month_manage,0) + IFNULL(mri.month_manage_adjust,0) + ");
    	sb.append(" IFNULL(mri.overdue_manage,0) + IFNULL(mri.overdue_manage_adjust,0) + IFNULL(mri.overdue_fine,0) + IFNULL(mri.revenue,0) AS current_should_refund, ");
    	
		sb.append(" mri.principal,                                                                                               ");
		sb.append(" mri.interest,                                                                                                ");
		sb.append(" mri.current_period                                                                                                ");
		//sb.append(" SUBSTRING_INDEX(mri.current_period, '/', -1)-SUBSTRING_INDEX(mri.current_period, '/', 1) AS current_period   ");

    	sb.append(" from member_refund_info mri, audit_bid_info abi WHERE mri.mb_id = abi.m_bid AND mri.`status` = 1 AND abi.global_status <> 40 GROUP BY mb_id ");
    	sb.append(" UNION ALL ");
    	sb.append(" SELECT a.id, a.mb_id, a.`status`, a.refund_date, a.overdue_day, a.s AS begin_should_refund, CASE WHEN b.mb_id IS NULL THEN IFNULL(a.s,0) ELSE IFNULL(a.s,0) + IFNULL(b.t,0) END AS current_should_refund, ");
    	
		sb.append(" a.principal, ");
		sb.append(" a.interest,        ");
		sb.append(" a.current_period ");
		
    	sb.append(" from(SELECT MIN(mri.id) AS id, mb_id, SUM(IFNULL(mri.principal_interest,0) + IFNULL(mri.month_manage,0) + IFNULL(mri.overdue_manage,0) + IFNULL(mri.overdue_fine,0)) AS s, ");
    	sb.append(" mri.`status`, mri.refund_date, mri.overdue_day, " );
	    sb.append("    	mri.principal,                                                                                             ");
	    sb.append("     mri.interest,                                                                                              ");
	    sb.append("     mri.current_period ");
//	    sb.append("     SUBSTRING_INDEX(mri.current_period, '/', -1)-SUBSTRING_INDEX(mri.current_period, '/', 1) AS current_period ");
    	
	    sb.append("		from member_refund_info mri, audit_bid_info abi ");                                                        
    	sb.append(" WHERE mri.refund_date>'2016-7-10' and mri.refund_date<'2016-7-20' and refund_date is not null and mri.mb_id = abi.m_bid AND mri.`status` = 1 AND abi.global_status = 40 GROUP BY mri.mb_id) a ");
    	sb.append(" LEFT JOIN (SELECT mb_id, SUM(IFNULL(principal,0) + IFNULL(interest,0) + IFNULL(overdue_manage,0) + IFNULL(overdue_fine,0) + IFNULL(month_manage,0)) AS t ");
    	sb.append(" from member_refund_adjust GROUP BY mb_id) b ON a.mb_id = b.mb_id) mri ");
    	sb.append(" ON mri.mb_id = mbi.id ");
    	
    	sb.append(" LEFT JOIN (SELECT mb_id, SUM(IFNULL(should_principal_interest,0) + IFNULL(is_principal_interest,0) + IFNULL(ss_overdue_fines,0) + IFNULL(is_overdue_fines,0) + IFNULL(ss_late_fees,0) + IFNULL(is_late_fees,0) + IFNULL(should_manage_fee,0) + IFNULL(is_manage_fee,0) + IFNULL(ss_prepayment_penalties,0) + IFNULL(is_prepayment_penalties,0)) AS begin_refund_total, ");
        sb.append(" SUM(IFNULL(adjust_should_principal_interest,0) + IFNULL(is_principal_interest,0) + IFNULL(adjuest_ss_overdue_fines,0) + IFNULL(is_overdue_fines,0) + IFNULL(adjuest_ss_late_fees,0) + IFNULL(is_late_fees,0) + IFNULL(adjuest_should_manage_fee,0) + IFNULL(is_manage_fee,0) + IFNULL(ss_prepayment_penalties,0) + IFNULL(is_prepayment_penalties,0)) AS should_refund_total, ");
        sb.append(" SUM(IFNULL(should_principal_interest,0) + IFNULL(ss_overdue_fines,0) + IFNULL(ss_late_fees,0) + IFNULL(should_manage_fee,0) + IFNULL(ss_prepayment_penalties,0)) AS begin_daihuan_refund_total, SUM(IFNULL(adjust_should_principal_interest,0) + IFNULL(adjuest_ss_overdue_fines,0) + IFNULL(adjuest_ss_late_fees,0) + IFNULL(adjuest_should_manage_fee,0) + IFNULL(ss_prepayment_penalties,0)) as daihuan_refund_total ");
        sb.append(" from member_refund_total_info GROUP BY mb_id) mrti ON mrti.mb_id = mbi.id ");
        sb.append(" LEFT JOIN hdb_product_info hpi ON mbi.product_id = hpi.id ");
        sb.append(" LEFT JOIN member m ON mbi.m_id = m.id ");
    	 
    	sb.append(" LEFT JOIN member_realname_auth mra ON m.auth_id = mra.id ");
        sb.append(" LEFT JOIN member_refund_log mrl ON mri.id = mrl.mr_id ");
        sb.append(" LEFT JOIN sys_user su ON mrl.urge_oper_uid = su.id ");
        sb.append(" WHERE 1=1 ");
 		
 		StringBuffer newSb = new StringBuffer();
 		newSb.append("SELECT sysUser.`name` responsiblePerson, tt.* from ( ");
 		newSb.append(sb);
 		newSb.append(" ) tt LEFT JOIN sys_user sysUser ON tt.responsible_uid = sysUser.id");
		return sb.toString();
    }
    
    public static void main(String[] args) {
		System.out.println(setSqlStr());
	}
}
