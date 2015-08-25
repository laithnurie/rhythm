package com.laithlab.core.echonest;

public class Status
{
	private String message;

	private String code;

	private String version;

	public String getMessage ()
	{
		return message;
	}

	public void setMessage (String message)
	{
		this.message = message;
	}

	public String getCode ()
	{
		return code;
	}

	public void setCode (String code)
	{
		this.code = code;
	}

	public String getVersion ()
	{
		return version;
	}

	public void setVersion (String version)
	{
		this.version = version;
	}

	@Override
	public String toString()
	{
		return "ClassPojo [message = "+message+", code = "+code+", version = "+version+"]";
	}
}