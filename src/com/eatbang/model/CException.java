package com.eatbang.model;

public class CException {
	
	public static final int EXCEPTION_CODE_ENTITY_NOT_FOUND = 1;
	public static final int EXCEPTION_CODE_USER_EXISTS = 2;
	public static final int EXCEPTION_CODE_ALREADY_LOGIN = 3;
	public static final int EXCEPTION_CODE_VALIDATION_ERROR = 4;
	public static final int EXCEPTION_CODE_ADD_FRIEND_ERROR = 5;
	
	public String entityName;
	
	public int code;
	
	public String description;
	
	public CException(){}
	
	public CException(int code)
	{
		this(code, null);
	}
	
	public CException ( int code, String entityName)
	{
		this.entityName = entityName;
		this.code = code;
		
		switch(code){
			case CException.EXCEPTION_CODE_ENTITY_NOT_FOUND:
				if(entityName == null)
					entityName = "Unknown entity";
				this.description = entityName + " not found.";
				break;
			case CException.EXCEPTION_CODE_USER_EXISTS:
				this.description = "User already exists";
				break;
			case CException.EXCEPTION_CODE_ALREADY_LOGIN:
				this.description = "Already login.";
				break;
			case CException.EXCEPTION_CODE_VALIDATION_ERROR:
				this.description = entityName + " validation error.";
				break;
			case CException.EXCEPTION_CODE_ADD_FRIEND_ERROR:
				this.description = "add friend validation error.";
				break;
		}
	}

}
