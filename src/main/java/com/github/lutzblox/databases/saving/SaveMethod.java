package com.github.lutzblox.databases.saving;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.utils.ExtendedMap;

public interface SaveMethod {

	public ExtendedMap load(Listenable l);
	
	public void save(ExtendedMap data, Listenable l);
}
