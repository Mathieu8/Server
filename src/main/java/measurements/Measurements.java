package measurements;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * A simple example of Measurements class, later it should be increased to a
 * complete Measurements. <br>
 * <br>
 * 
 * setters should also auto increase their counter here.<br>
 * <br>
 * 
 * <b> TODO</b> setters also save a timestamp
 * 
 * @author Mathieu
 * @version 08/23/2018
 *
 */
public class Measurements  implements BasicMeasurements {
	private static final long serialVersionUID = 1L;
	private LocalDateTime BeginDateTime;
	private long beginTime, endTime, duration;

	private String emotion;
	private int emotionCounter;

	private int intesitity = 127;
	private int intesitityCounter;

	private String activity;
	private int activityCounter;

	private int productivity;
	private int productivityCounter;

	public Measurements(int UID, LocalDateTime dt) {
//		this.UID = UID;
		this.BeginDateTime = dt;
		beginTime = System.currentTimeMillis();
	}

	public void setDuraction() {
		endTime = System.currentTimeMillis();
		duration = endTime - beginTime;
	}

	@Override
	public String toString() {
		String temp = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		return (String) temp.subSequence(13, temp.length()-1);
	}

	@Override
	public String getTableName() {
		return "meetresultaat";
	}

	public void setBeginDateTime(LocalDateTime beginDateTime) {
		BeginDateTime = beginDateTime;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
		emotionCounter++;
		System.out.println("in measurements " + this.emotion);
	}

	public void setIntesitity(int intesitity) {
		intesitityCounter++;
		this.intesitity = intesitity;
	}

	public void setUID(int uID) {
//		UID = uID;
	}

	public void setActivity(String activity) {
		this.activity = activity;
		activityCounter++;
	}

	public void setProductivity(int productivity) {
		this.productivity = productivity;
		productivityCounter++;
	}
}