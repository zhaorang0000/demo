<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>登录</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="jquery-1.9.1.min.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style type="text/css">
	body {
	    text-align: center;
	}
	p{
	    background-color: #436EEE;
	    height: 20px;
	    color: white;
	    font-weight: bold;
	    margin-top: 0px;
	}
	b{
	    margin-top: 20px;
	}
	.content{
	    width: 50%;
	    margin-left: 25%;
	    border-color: black;
	    border-style: solid;
	    border-width: 1px;
	    margin-top: 150px;
	}
	.input{
	    margin-top: 20px;
	}
	.butt{
	    margin-top: 20px;
	    margin-bottom: 80px;
	}
	</style>
  </head>
  
  <body>
     <div class="content">
             <p>登录</p>
	         <b>CORExGANBEI管理系统</b>
	         <div class="input">
	             <span>密&nbsp;码:</span><input id="userName" type="text"/>
	         </div>
	         <div class="input">
	             <span>用户名:</span><input id="pwd" type="password"/>
	         </div>
	         <div class="butt">
	             <button id="login">登录</button>&nbsp;&nbsp;<button id="reset">重置</button>
	         </div>
     </div>
  </body>
<script type="text/javascript">
//注册重置事件
$("#reset").click(function(){
    $("#userName").val("");
    $("#pwd").val("");
});
//注册登录事件
$("#login").click(function(){
    
   var userName=$("#userName").val();
   if(userName!=null&&userName!="")
   {
       var pwd=$("#pwd").val();
       if(pwd!=null&&pwd!="")
       {
           $.ajax({
               url:"/LoginDemo/LoginServlet",
               data:{
                   "userName":userName,
                   "pwd":pwd
               },
               dataType:"text",
               type:"post",
               success:function(data)
               {
                   if(data=="200")
                   {
                       alert("登录成功！");
                   }
                   else
                   {
                       alert("账号不存在或密码错误！");
                   }
               }
           });
       }
       else
       {
           alert("请输入密码!");
       }
   }
   else
   {
       alert("请输入用户名!");
   }
});
</script>
</html>
