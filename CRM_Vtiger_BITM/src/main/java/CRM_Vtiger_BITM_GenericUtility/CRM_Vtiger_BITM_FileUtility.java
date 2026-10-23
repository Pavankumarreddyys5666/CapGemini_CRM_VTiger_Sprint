
package CRM_Vtiger_BITM_GenericUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CRM_Vtiger_BITM_FileUtility {

	private static final String CONFIG_FILE = "CRM_Vtiger_BITM_CommonData.Properties";

	public String getPropertyKeyValue(String key) throws IOException {

		Properties properties = new Properties();

		try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {

			if (input == null) {
				throw new IOException("Configuration file not found: " + CONFIG_FILE);
			}

			properties.load(input);
		}

		String value = properties.getProperty(key);

		if (value == null || value.trim().isEmpty()) {
			throw new IOException("Missing or empty property: " + key);
		}

		return value.trim();
	}
}
