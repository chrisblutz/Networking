package com.github.lutzblox.query;

import com.github.lutzblox.Listenable;


/**
 * @author Christopher Lutz
 */
public interface QueryListener {

    Object onQuery(Listenable listenable);
}
