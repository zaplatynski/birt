/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.data.engine.olap.util.filter;

import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.ScriptContext;
import org.eclipse.birt.core.script.ScriptExpression;
import org.eclipse.birt.data.engine.api.IBaseQueryResults;
import org.eclipse.birt.data.engine.api.IFilterDefinition;
import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.engine.olap.api.query.ICubeQueryDefinition;
import org.eclipse.birt.data.engine.olap.data.api.DimLevel;
import org.eclipse.birt.data.engine.olap.data.util.CompareUtil;
import org.eclipse.birt.data.engine.olap.util.IJSObjectPopulator;
import org.eclipse.birt.data.engine.script.ScriptEvalUtil;
import org.mozilla.javascript.Scriptable;

/**
 * This is a helper class which provide script evaluation services for dimension filter.
 */ 
public class DimensionFilterEvalHelper extends 
BaseDimensionFilterEvalHelper implements IJSDimensionFilterHelper
{
	/**
	 * 
	 * @param outResults
	 * @param parentScope
	 * @param queryDefn
	 * @param cubeFilter
	 * @throws DataException
	 */
	public DimensionFilterEvalHelper( IBaseQueryResults outResults, Scriptable parentScope, ScriptContext cx, ICubeQueryDefinition queryDefn, IFilterDefinition cubeFilter) throws DataException
	{
		assert cubeFilter!=null;
		initialize( outResults, parentScope, queryDefn, cubeFilter, cx );
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.engine.olap.util.filter.IJsFilter#evaluateFilter(org.eclipse.birt.data.engine.olap.util.filter.IResultRow)
	 */
	public boolean evaluateFilter( IResultRow resultRow )
			throws DataException
	{

		super.setData( resultRow );
		
		try
		{
			if ( this.isAxisFilter )
			{
				for ( int i = 0; i < axisLevels.length; i++ )
				{
					DimLevel level = new DimLevel( axisLevels[i] );
					if ( CompareUtil.compare( resultRow.getFieldValue( level.toString( ) ),
							axisValues[i] ) != 0 )
					{
						return false;
					}
				}
			}
			Object result = ScriptEvalUtil.evalExpr( expr, cx, scope, ScriptExpression.defaultID, 0 );
			return DataTypeUtil.toBoolean( result ).booleanValue( );
		}
		catch ( IJSObjectPopulator.InMatchDimensionIndicator e )
		{
			return true;
		}
		catch ( BirtException e )
		{
			throw DataException.wrap( e );
		}
	}
}
