package measurements;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * All Measurements should implants this interface. As it does makes it possible
 * for the SaveMethod class to correctly fill in the data to the DB
 * 
 * @author Mathieu
 * @version 08/06/2018
 * 
 */
public interface BasicMeasurements extends java.io.Serializable {

	/**
	 * 
	 * @return the name of the DB table the data should go into
	 */
	public abstract String getTableName();

	/**
	 * This method returns all the fields and their corrosponding data from the
	 * class that implemts BasicMeasurements. It should still be edited a bit before
	 * it can be used to extract SQL insertion here
	 * 
	 * 
	 * 
	 * 
	 * @return String array with all the data in it
	 * 
	 */
	public default String getData() {

		String temp = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		return (String) temp.subSequence(13, temp.length() - 1);

	}

	// public abstract String[] getDataa();
	public abstract void setDuraction();

}
