/*
 * Distributed as part of c3p0 v.0.9.5-pre1
 *
 * Copyright (C) 2013 Machinery For Change, Inc.
 *
 * Author: Steve Waldman <swaldman@mchange.com>
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of EITHER:
 *
 *     1) The GNU Lesser General Public License (LGPL), version 2.1, as 
 *        published by the Free Software Foundation
 *
 * OR
 *
 *     2) The Eclipse Public License (EPL), version 1.0
 *
 * You may choose which license to accept if you wish to redistribute
 * or modify this work. You may offer derivatives of this work
 * under the license you have chosen, or you may provide the same
 * choice of license which you have been offered here.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received copies of both LGPL v2.1 and EPL v1.0
 * along with this software; see the files LICENSE-EPL and LICENSE-LGPL.
 * If not, the text of these licenses are currently available at
 *
 * LGPL v2.1: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  EPL v1.0: http://www.eclipse.org/org/documents/epl-v10.php 
 * 
 */

package com.mchange.v2.c3p0.stmt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.lang.reflect.Method;

final class SimpleStatementCacheKey extends StatementCacheKey
{
    static StatementCacheKey _find( Connection pcon, Method stmtProducingMethod, Object[] args )
    {
	///BEGIN FIND LOGIC///
	String stmtText = (String) args[0];
	boolean is_callable = stmtProducingMethod.getName().equals("prepareCall");
	int result_set_type;
	int result_set_concurrency;

	int[] columnIndexes;
	String[] columnNames;
	Integer autogeneratedKeys;
	Integer resultSetHoldability;

	if (args.length == 1)
	    {
		result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
		result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = null;
	    }
	else if (args.length == 2)
	    {
		Class[] argTypes = stmtProducingMethod.getParameterTypes();
		if (argTypes[1].isArray())
		    {
			Class baseType = argTypes[1].getComponentType();
			if (baseType == int.class) //second arg is columnIndexes
			    {
				result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
				result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
				columnIndexes          = (int[]) args[1];
				columnNames            = null;
				autogeneratedKeys      = null;
				resultSetHoldability   = null;
			    }
			else if (baseType == String.class)
			    {
				result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
				result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
				columnIndexes          = null;
				columnNames            = (String[]) args[1];
				autogeneratedKeys      = null;
				resultSetHoldability   = null;
			    }
			else
			    throw new IllegalArgumentException("c3p0 probably needs to be updated for some new " +
							       "JDBC spec! As of JDBC3, we expect two arg statement " +
							       "producing methods where the second arg is either " +
							       "an int, int array, or String array.");
		    }
		else //it should be a boxed int, autogeneratedKeys
		    {
			result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
			result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
			columnIndexes          = null;
			columnNames            = null;
			autogeneratedKeys      = (Integer) args[1];
			resultSetHoldability   = null;
		    }
	    }
	else if (args.length == 3)
	    {
		result_set_type        = ((Integer) args[1]).intValue();
		result_set_concurrency = ((Integer) args[2]).intValue();
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = null;
	    }
	else if (args.length == 4)
	    {
		result_set_type        = ((Integer) args[1]).intValue();
		result_set_concurrency = ((Integer) args[2]).intValue();
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = (Integer) args[3];
	    }
	else
	    throw new IllegalArgumentException("Unexpected number of args to " + 
					       stmtProducingMethod.getName() );
	///END FIND LOGIC///


	return new SimpleStatementCacheKey( pcon, 
					    stmtText, 
					    is_callable, 
					    result_set_type, 
					    result_set_concurrency,
					    columnIndexes,
					    columnNames,
					    autogeneratedKeys,
					    resultSetHoldability );
    }

    SimpleStatementCacheKey( Connection physicalConnection,
			     String stmtText,
			     boolean is_callable,
			     int result_set_type,
			     int result_set_concurrency,
			     int[] columnIndexes,
			     String[] columnNames,
			     Integer autogeneratedKeys,
			     Integer resultSetHoldability )
    {
	super( physicalConnection,
	       stmtText,
	       is_callable,
	       result_set_type,
	       result_set_concurrency,
	       columnIndexes,
	       columnNames,
	       autogeneratedKeys,
	       resultSetHoldability );
    }

    public boolean equals( Object o )
    { return StatementCacheKey.equals( this, o ); }

    public int hashCode()
    { return StatementCacheKey.hashCode( this ); }
}
