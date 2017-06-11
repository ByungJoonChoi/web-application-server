package util;

import db.DataBase;
import model.User;

public class HTMLUtil {
	public static String getUserList(){
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"></head><body>");
		sb.append("<ul>");
		for(User user : DataBase.findAll()){
			sb.append("<li>" + user.getName() + "</li>");
		}
		sb.append("</ul></body></html>");
		return sb.toString();
	}
}
