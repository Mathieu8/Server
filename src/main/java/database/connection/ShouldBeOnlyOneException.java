package database.connection;

import java.sql.SQLDataException;

/**
 * @author Mathieu
 *
 */
public class ShouldBeOnlyOneException extends SQLDataException {

	/**
	 * 4/3/2019
	 */
	private static final long serialVersionUID = -8811830938646987024L;

	public ShouldBeOnlyOneException() {
	}

	public ShouldBeOnlyOneException(String string) {
	}

	public ShouldBeOnlyOneException(Throwable throwable) {
	}

	public ShouldBeOnlyOneException(String string, Throwable throwable) {
	}

}
