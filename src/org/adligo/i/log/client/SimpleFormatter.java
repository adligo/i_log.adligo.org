package org.adligo.i.log.client;

import org.adligo.i.log.client.models.FormatItem;
import org.adligo.i.log.client.models.I_LogMessage;
import org.adligo.i.log.client.models.LogMessage;
import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Iterator;

public class SimpleFormatter implements I_Formatter {
	public static final String DEFAULT_FORMAT_STRING = "[%p] %c{1} - %m";
	
	private String asString = "";
	private I_Collection items = CollectionFactory.create();
	
	public SimpleFormatter() {
		setFormatString(DEFAULT_FORMAT_STRING);
	}
	
	public String format(I_LogMessage message) {
		StringBuffer sb = new StringBuffer();
		I_Iterator it = items.getIterator();
		while (it.hasNext()) {
			FormatItem item = (FormatItem) it.next();
			if (LogPlatform.isDebug()) {
				LogPlatform.log("SimpleFormatter", " FormatItem is " + item);
			}
			switch (item.getType()) {
				case FormatItem.STRING_TYPE:
					sb.append(item.getAsString());
					break;
				case FormatItem.THREAD_TYPE:
					sb.append(message.getThread());
					break;
				case FormatItem.PRIORITY_TYPE:
					sb.append(message.getLevelString());
					break;
				case FormatItem.MESSAGE_TYPE:
					sb.append(message.getMessage());
					break;
				case FormatItem.CATEGORY_TYPE:
					String name = message.getName();
					if (name != null && item.getCategory_precision() != 0) {
						StringBuffer nb = new StringBuffer();
						int prec = 0;
						char [] nameChars = name.toCharArray();
						for (int i = nameChars.length - 1; i >= 0; i--) {
							char c = nameChars[i];
							if (c == '.') {
								prec++;
							} if (prec >= item.getCategory_precision()) {
								//StringBuffer.reverse wasn't working for me?
								StringBuffer bb = new StringBuffer();
								char [] chars = nb.toString().toCharArray();
								for (int j = chars.length - 1; j >=0 ; j--) {
									bb.append(chars[j]);
								}
								name = bb.toString();
							} else {
								nb.append(c);
							}
						}
					}
					sb.append(name);
					break;	
			}
			
		}
		return sb.toString();
	}

	public synchronized String getFormatString() {
		return asString;
	}

	public synchronized void setFormatString(String format) {
		asString = format;
		char [] chars = asString.toCharArray();
		items.clear();
		boolean hadLead = false;
		boolean hadCategory = false;
		boolean hadBraket = false;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			/*
			if (LogPlatform.isDebug()) {
				LogPlatform.log("SimpleFormatter", " Character " + c + " had lead " + hadLead);
			}
			*/
			if (c == FormatItem.LEAD_CHAR) {
				if (sb.length() > 0) {
					FormatItem fi = new FormatItem(FormatItem.STRING_TYPE, sb.toString());
					fi.setAsString(sb.toString());
					sb = new StringBuffer();
					items.add(fi);
				}
				hadLead = true;
			} else if (hadLead) {
				if (c == FormatItem.LEAD_CHAR) {
					sb.append(FormatItem.LEAD_CHAR);
					hadLead = false;
				} else if (c == FormatItem.MESSAGE) {
					items.add(new FormatItem(FormatItem.MESSAGE_TYPE));
					sb = new StringBuffer();
					hadLead = false;
				} else if (c == FormatItem.PRIORITY) {
					items.add(new FormatItem(FormatItem.PRIORITY_TYPE));
					sb = new StringBuffer();
					hadLead = false;
				}  else if (c == FormatItem.THREAD) {
					items.add(new FormatItem(FormatItem.THREAD_TYPE));
					sb = new StringBuffer();
					hadLead = false;
				} else if (c == FormatItem.CATEGORY) {
					hadCategory = true;
				} else if (hadCategory) {
					if (c == FormatItem.BRACKET_START) {
						sb = new StringBuffer();
						hadBraket = true;
					} else if (c ==  FormatItem.BRACKET_END && hadBraket) {
						FormatItem fi = new FormatItem(FormatItem.CATEGORY_TYPE);
						if (sb.length() > 0) {
							try {
								
								if (LogPlatform.isDebug()) {
									LogPlatform.log("SimpleFormatter", "parsing " + sb.toString());
								}
								fi.setCategory_precision(
										new Integer(sb.toString()).shortValue());
								sb = new StringBuffer();
							} catch (Exception x) {
								x.printStackTrace();
							}
						}
						items.add(fi);
						hadLead = false;
						hadCategory = false;
						hadBraket = false;
					} else {
						if (hadBraket) {
							sb.append(c);
						} else {
							items.add(new FormatItem(FormatItem.CATEGORY_TYPE));
							hadLead = false;
							hadCategory = false;
							sb.append(c);
						} 
					}
					
					
				} else {
					if (sb.length() > 0) {
						FormatItem fi = new FormatItem(FormatItem.STRING_TYPE, sb.toString());
						fi.setAsString(sb.toString());
						sb = new StringBuffer();
						items.add(fi);
					} else {
						sb.append(c);
					}
				}
			} else {
				/*
				if (LogPlatform.isDebug()) {
					LogPlatform.log("SimpleFormatter", "appending " + c);
				}
				*/
				sb.append(c);
			}
			
		}
		
	}

	I_Iterator getItems() {
		return items.getIterator();
	}
}
