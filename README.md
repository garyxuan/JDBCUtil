# JDBCUtil
Java数据操作工具类，反射机制查询记录
![garyxuan-logo](http://a3.qpic.cn/psb?/V13cefHz22OeKo/zqOQVQhPTW8866VOdMoB37E5*550rDtDUtxy7ZX2WDo!/b/dNoAAAAAAAAA&bo=fQBbAAAAAAADBwQ!&rf=viewer_4)
------

##Installation
- Copy JDBCUtil.java to your project and rename the package name

------
##Usage
```java 
	String sql = "select *from city where countryCode = ?";
	JDBCUtil jdbcUtil = new JDBCUtil();
	jdbcUtil.getConnection();
	List<Object> param = new ArrayList<Object>();
	param.add("AFG");
	List<Map<String, Object>> list = null;
	try {
		list = jdbcUtil.executeQuery(sql, param);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	for (Map<String, Object> map : list) {
		System.out.println(map);
	}
	jdbcUtil.releaseConnectn();
    
    
	String sql = "update city set name = ? where id = ? or id = ?";
	JDBCUtil jdbcUtil = new JDBCUtil();
	jdbcUtil.getConnection();
	List<Object> param1 = new ArrayList<Object>();
	param1.add("garyxuan");
	param1.add(1);
	param1.add(2);
	int rows = 0;
	try {
		rows = jdbcUtil.executeUpdate(sql, param1);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(rows);
	jdbcUtil.releaseConnectn();
    
	String sql = "select *from city where countryCode = ?";
	JDBCUtil jdbcUtil = new JDBCUtil();
	jdbcUtil.getConnection();
	List<Object> param = new ArrayList<Object>();
	param.add("AFG");
	List<City> list = null;
	try {
		list = jdbcUtil.findMoreRefResult(sql, param, City.class);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(list.size());
	for (City city : list) {
		System.out.println(city.toString());
	}
	jdbcUtil.releaseConnectn();
```
-------

##Contact
> garyxuan@163.com




