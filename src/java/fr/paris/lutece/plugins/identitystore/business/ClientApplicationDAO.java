/*
 * Copyright (c) 2002-2016, Mairie de Paris
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


/**
 * This class provides Data Access methods for ClientApplication objects
 */
public final class ClientApplicationDAO implements IClientApplicationDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_client_app ) FROM identitystore_client_application";
    private static final String SQL_QUERY_SELECT = "SELECT id_client_app, name, code, hash, control_key FROM identitystore_client_application WHERE id_client_app = ?";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT id_client_app, name, code, hash, control_key FROM identitystore_client_application WHERE code = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identitystore_client_application ( id_client_app, name, code, hash, control_key ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identitystore_client_application WHERE id_client_app = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identitystore_client_application SET id_client_app = ?, name = ?, code = ?, hash = ?, control_key = ? WHERE id_client_app = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_client_app, name, code, hash, control_key FROM identitystore_client_application";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_client_app FROM identitystore_client_application";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *          The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ClientApplication clientApplication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        clientApplication.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, clientApplication.getId(  ) );
        daoUtil.setString( nIndex++, clientApplication.getName(  ) );
        daoUtil.setString( nIndex++, clientApplication.getCode(  ) );
        daoUtil.setString( nIndex++, clientApplication.getHash(  ) );
        daoUtil.setString( nIndex++, clientApplication.getControlKey(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ClientApplication load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        ClientApplication clientApplication = null;

        if ( daoUtil.next(  ) )
        {
            clientApplication = new ClientApplication(  );

            int nIndex = 1;

            clientApplication.setId( daoUtil.getInt( nIndex++ ) );
            clientApplication.setName( daoUtil.getString( nIndex++ ) );
            clientApplication.setCode( daoUtil.getString( nIndex++ ) );
            clientApplication.setHash( daoUtil.getString( nIndex++ ) );
            clientApplication.setControlKey( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );

        return clientApplication;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( ClientApplication clientApplication, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, clientApplication.getId(  ) );
        daoUtil.setString( nIndex++, clientApplication.getName(  ) );
        daoUtil.setString( nIndex++, clientApplication.getCode(  ) );
        daoUtil.setString( nIndex++, clientApplication.getHash(  ) );
        daoUtil.setString( nIndex++, clientApplication.getControlKey(  ) );
        daoUtil.setInt( nIndex, clientApplication.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ClientApplication> selectClientApplicationList( Plugin plugin )
    {
        List<ClientApplication> clientApplicationList = new ArrayList<ClientApplication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ClientApplication clientApplication = new ClientApplication(  );
            int nIndex = 1;

            clientApplication.setId( daoUtil.getInt( nIndex++ ) );
            clientApplication.setName( daoUtil.getString( nIndex++ ) );
            clientApplication.setCode( daoUtil.getString( nIndex++ ) );
            clientApplication.setHash( daoUtil.getString( nIndex++ ) );
            clientApplication.setControlKey( daoUtil.getString( nIndex++ ) );
            clientApplicationList.add( clientApplication );
        }

        daoUtil.free(  );

        return clientApplicationList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdClientApplicationList( Plugin plugin )
    {
        List<Integer> clientApplicationList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            clientApplicationList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return clientApplicationList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectClientApplicationReferenceList( Plugin plugin )
    {
        ReferenceList clientApplicationList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            clientApplicationList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return clientApplicationList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ClientApplication selectByCode( String strCode, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CODE, plugin );
        daoUtil.setString( 1, strCode );
        daoUtil.executeQuery(  );

        ClientApplication clientApplication = null;

        if ( daoUtil.next(  ) )
        {
            clientApplication = new ClientApplication(  );

            int nIndex = 1;

            clientApplication.setId( daoUtil.getInt( nIndex++ ) );
            clientApplication.setName( daoUtil.getString( nIndex++ ) );
            clientApplication.setCode( daoUtil.getString( nIndex++ ) );
            clientApplication.setHash( daoUtil.getString( nIndex++ ) );
            clientApplication.setControlKey( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );

        return clientApplication;
    }
}
