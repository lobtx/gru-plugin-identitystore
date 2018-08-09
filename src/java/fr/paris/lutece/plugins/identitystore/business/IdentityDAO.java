/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.identitystore.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * This class provides Data Access methods for Identity objects
 */
public final class IdentityDAO implements IIdentityDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_identity ) FROM identitystore_identity";
    private static final String SQL_QUERY_SELECT = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity WHERE id_identity = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identitystore_identity ( id_identity, connection_id, customer_id ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identitystore_identity WHERE id_identity = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identitystore_identity SET id_identity = ?, connection_id = ?, customer_id = ? WHERE id_identity = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity";
    private static final String SQL_QUERY_SELECTALL_CUSTOMER_IDS = "SELECT customer_id FROM identitystore_identity";
    private static final String SQL_QUERY_SELECTALL_CUSTOMER_IDS_WITH_LIMIT = "SELECT customer_id FROM identitystore_identity ORDER BY id_identity ASC LIMIT ?, ?";
    private static final String SQL_QUERY_SELECT_BY_CONNECTION_ID = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity WHERE connection_id = ?";
    private static final String SQL_QUERY_SELECT_BY_CUSTOMER_ID = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity WHERE customer_id = ?";
    private static final String SQL_QUERY_SELECT_ID_BY_CONNECTION_ID = "SELECT id_identity FROM identitystore_identity WHERE connection_id = ?";
    private static final String SQL_QUERY_SELECT_BY_ATTRIBUTE = "SELECT DISTINCT a.id_identity, a.connection_id, a.customer_id "
            + " FROM identitystore_identity a,  identitystore_identity_attribute b " + " WHERE a.id_identity = b.id_identity AND b.attribute_value ";
    private static final String SQL_QUERY_FILTER_ATTRIBUTE = " AND b.id_attribute = ? ";
    private static final String SQL_QUERY_SELECT_ALL_BY_CONNECTION_ID = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity WHERE connection_id ";
    private static final String SQL_QUERY_SELECT_ALL_BY_CUSTOMER_ID = "SELECT id_identity, connection_id, customer_id FROM identitystore_identity WHERE customer_id ";
    private static final String SQL_QUERY_SELECT_BY_ALL_ATTRIBUTES_CID_GUID_LIKE = "SELECT DISTINCT a.id_identity, a.connection_id, a.customer_id  FROM identitystore_identity a,  identitystore_identity_attribute b "
            + " WHERE (a.id_identity = b.id_identity AND b.attribute_value LIKE ? )" + " OR a.customer_id LIKE ? OR a.connection_id LIKE ?";
    private static final String SQL_QUERY_SELECT_BY_ALL_ATTRIBUTES_CID_GUID = "SELECT DISTINCT a.id_identity, a.connection_id, a.customer_id  FROM identitystore_identity a,  identitystore_identity_attribute b "
            + " WHERE (a.id_identity = b.id_identity AND b.attribute_value = ? )" + " OR a.customer_id = ? OR a.connection_id = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    private synchronized int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    /**
     * Generates a new customerId key using Java UUID
     *
     * @return The new customerID
     */
    public String newCustomerIdKey( )
    {
        return UUID.randomUUID( ).toString( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Identity identity, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        identity.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;
        identity.setCustomerId( newCustomerIdKey( ) );
        daoUtil.setInt( nIndex++, identity.getId( ) );
        daoUtil.setString( nIndex++, identity.getConnectionId( ) );
        daoUtil.setString( nIndex++, identity.getCustomerId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Identity load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        Identity identity = null;

        if ( daoUtil.next( ) )
        {
            identity = new Identity( );

            int nIndex = 1;

            identity.setId( daoUtil.getInt( nIndex++ ) );
            identity.setConnectionId( daoUtil.getString( nIndex++ ) );
            identity.setCustomerId( daoUtil.getString( nIndex++ ) );

        }

        daoUtil.free( );

        if ( identity != null )
        {
            identity.setAttributes( IdentityAttributeHome.getAttributes( identity.getId( ) ) );
        }

        return identity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Identity identity, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, identity.getId( ) );
        daoUtil.setString( nIndex++, identity.getConnectionId( ) );
        daoUtil.setString( nIndex++, identity.getCustomerId( ) );
        daoUtil.setInt( nIndex, identity.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectCustomerIdsList( Plugin plugin )
    {
        List<String> listIds = new ArrayList<String>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_CUSTOMER_IDS, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            String identity = daoUtil.getString( 1 );
            listIds.add( identity );
        }

        daoUtil.free( );

        return listIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectCustomerIdsList( int nStart, int nLimit, Plugin plugin )
    {
        List<String> listIds = new ArrayList<String>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_CUSTOMER_IDS_WITH_LIMIT, plugin );
        daoUtil.setInt( 1, nStart );
        daoUtil.setInt( 2, nLimit );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            String identity = daoUtil.getString( 1 );
            listIds.add( identity );
        }

        daoUtil.free( );

        return listIds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectIdentitysReferenceList( Plugin plugin )
    {
        ReferenceList identityList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            identityList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return identityList;
    }

    @Override
    public Identity selectByConnectionId( String strConnectionId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CONNECTION_ID, plugin );
        daoUtil.setString( 1, strConnectionId );
        daoUtil.executeQuery( );

        Identity identity = null;

        if ( daoUtil.next( ) )
        {
            identity = getIdentityFromQuery( daoUtil );
        }

        daoUtil.free( );

        return identity;
    }

    @Override
    public Identity selectByCustomerId( String strCustomerId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CUSTOMER_ID, plugin );
        daoUtil.setString( 1, strCustomerId );
        daoUtil.executeQuery( );

        Identity identity = null;

        if ( daoUtil.next( ) )
        {
            identity = getIdentityFromQuery( daoUtil );
        }

        daoUtil.free( );

        return identity;
    }

    @Override
    public int selectIdByConnectionId( String strConnectionId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_BY_CONNECTION_ID, plugin );
        daoUtil.setString( 1, strConnectionId );
        daoUtil.executeQuery( );

        int nIdentityId = -1;

        if ( daoUtil.next( ) )
        {
            nIdentityId = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nIdentityId;
    }

    /**
     * return Identity object from select query
     *
     * @param daoUtil
     *            daoUtil initialized with select query
     * @return Identity load from result
     */
    private Identity getIdentityFromQuery( DAOUtil daoUtil )
    {
        Identity identity = new Identity( );

        int nIndex = 1;

        identity.setId( daoUtil.getInt( nIndex++ ) );
        identity.setConnectionId( daoUtil.getString( nIndex++ ) );
        identity.setCustomerId( daoUtil.getString( nIndex++ ) );

        return identity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Identity> selectByAttributeValue( String strAttributeId, String strAttributeValue, Plugin plugin )
    {
        List<Identity> listIdentities = new ArrayList<Identity>( );
        String strSQL = SQL_QUERY_SELECT_BY_ATTRIBUTE;
        String strValue = strAttributeValue;
        if ( strAttributeValue.contains( "*" ) )
        {
            strValue = strValue.replace( '*', '%' );
            strSQL += " LIKE ?";
        }
        else
        {
            strSQL += " = ?";
        }

        if ( StringUtils.isNotEmpty( strAttributeId ) )
        {
            strSQL += SQL_QUERY_FILTER_ATTRIBUTE;
        }

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setString( 1, strValue );

        if ( StringUtils.isNotEmpty( strAttributeId ) )
        {
            daoUtil.setString( 2, strAttributeId );
        }
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Identity identity = getIdentityFromQuery( daoUtil );
            listIdentities.add( identity );
        }

        daoUtil.free( );

        return listIdentities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Identity> selectByAllAttributesValue( String strAttributeValue, Plugin plugin )
    {
        List<Identity> listIdentities = new ArrayList<Identity>( );
        String strSQL = SQL_QUERY_SELECT_BY_ALL_ATTRIBUTES_CID_GUID;

        String strFinalAttributeValue = strAttributeValue;
        if ( strAttributeValue.contains( "*" ) )
        {
            strFinalAttributeValue = strAttributeValue.replace( '*', '%' );
            strSQL = SQL_QUERY_SELECT_BY_ALL_ATTRIBUTES_CID_GUID_LIKE;
        }

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setString( 1, strFinalAttributeValue );
        daoUtil.setString( 2, strFinalAttributeValue );
        daoUtil.setString( 3, strFinalAttributeValue );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Identity identity = getIdentityFromQuery( daoUtil );
            listIdentities.add( identity );
        }

        daoUtil.free( );

        return listIdentities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Identity> selectAllByCustomerId( String strCustomerId, Plugin plugin )
    {
        List<Identity> listIdentities = new ArrayList<Identity>( );
        String strSQL = SQL_QUERY_SELECT_ALL_BY_CUSTOMER_ID;

        String strFinalCustomerId = strCustomerId;
        if ( strCustomerId.contains( "*" ) )
        {
            strFinalCustomerId = strCustomerId.replace( '*', '%' );
            strSQL += "LIKE ?";
        }
        else
        {
            strSQL += "= ?";
        }

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setString( 1, strFinalCustomerId );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Identity identity = getIdentityFromQuery( daoUtil );
            listIdentities.add( identity );
        }

        daoUtil.free( );

        return listIdentities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Identity> selectAllByConnectionId( String strConnectionId, Plugin plugin )
    {
        List<Identity> listIdentities = new ArrayList<Identity>( );
        String strSQL = SQL_QUERY_SELECT_ALL_BY_CONNECTION_ID;

        String strFinalConnectionId = strConnectionId;
        if ( strConnectionId.contains( "*" ) )
        {
            strFinalConnectionId = strConnectionId.replace( '*', '%' );
            strSQL += "LIKE ?";
        }
        else
        {
            strSQL += "= ?";
        }

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setString( 1, strFinalConnectionId );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Identity identity = getIdentityFromQuery( daoUtil );
            listIdentities.add( identity );
        }

        daoUtil.free( );

        return listIdentities;
    }
}
