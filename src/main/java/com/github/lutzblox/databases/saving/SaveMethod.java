package com.github.lutzblox.databases.saving;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.utils.ExtendedMap;

/**
 * A class to handle saving database information from a {@code DatabaseServer}
 * 
 * @author Christopher Lutz
 */
public interface SaveMethod {

	/**
	 * Loads the database information
	 * 
	 * @param l
	 *            The listenable sending the load request
	 * @return The database information in the form of a {@code ExtendedMap}
	 */
	public ExtendedMap load(Listenable l);

	/**
	 * Saves the database information
	 * 
	 * @param data
	 *            The data to save
	 * @param l
	 *            The listenable sending the save request
	 */
	public void save(ExtendedMap data, Listenable l);
}
