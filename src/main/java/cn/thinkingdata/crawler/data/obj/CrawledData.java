package cn.thinkingdata.crawler.data.obj;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class CrawledData {

	public String toLocalFileLine() throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sb = new StringBuilder();
		Field fields[] = getClass().getDeclaredFields();
		for(Field field : fields){
			if(Modifier.isStatic(field.getModifiers())){
				continue;
			}
			
			Object obj = field.get(this);
			if(obj != null){
				sb.append(String.valueOf(obj));
			}else{
				if(field.getType() == String.class){
					sb.append("");			
				}else if(field.getType() == Integer.class || field.getType() == Long.class 
						|| field.getType() == Double.class || field.getType() == Float.class){
					sb.append("0");								
				}else{
					sb.append("");
				}
			}
			sb.append("\t");
		}
		if(sb.length() >= 1){
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}
