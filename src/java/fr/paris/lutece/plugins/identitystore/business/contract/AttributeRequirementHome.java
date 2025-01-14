/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.identitystore.business.contract;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for AttributeRequirement objects
 */
public final class AttributeRequirementHome
{
    // Static variable pointed at the DAO instance
    private static IAttributeRequirementDAO _dao = SpringContextService.getBean( "identitystore.attributeRequirementDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identitystore" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AttributeRequirementHome( )
    {
    }

    /**
     * Create an instance of the attributeRequirement class
     * 
     * @param attributeRequirement
     *            The instance of the AttributeRequirement which contains the informations to store
     * @return The instance of attributeRequirement which has been created with its primary key.
     */
    public static AttributeRequirement create( AttributeRequirement attributeRequirement, ServiceContract serviceContract )
    {
        _dao.insert( attributeRequirement, serviceContract.getId( ), _plugin );

        return attributeRequirement;
    }

    /**
     * Update of the attributeRequirement which is specified in parameter
     * 
     * @param attributeRequirement
     *            The instance of the AttributeRequirement which contains the data to store
     * @return The instance of the attributeRequirement which has been updated
     */
    public static AttributeRequirement update( AttributeRequirement attributeRequirement )
    {
        _dao.store( attributeRequirement, _plugin );

        return attributeRequirement;
    }

    /**
     * Remove the attributeRequirement whose identifier is specified in parameter
     * 
     * @param nKey
     *            The attributeRequirement Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a attributeRequirement whose identifier is specified in parameter
     * 
     * @param nKey
     *            The attributeRequirement primary key
     * @return an instance of AttributeRequirement
     */
    public static Optional<AttributeRequirement> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the attributeRequirement objects and returns them as a list
     * 
     * @return the list which contains the data of all the attributeRequirement objects
     */
    public static List<AttributeRequirement> getAttributeRequirementsList( )
    {
        return _dao.selectAttributeRequirementsList( _plugin );
    }

    /**
     * Load the id of all the attributeRequirement objects and returns them as a list
     * 
     * @return the list which contains the id of all the attributeRequirement objects
     */
    public static List<Integer> getIdAttributeRequirementsList( )
    {
        return _dao.selectIdAttributeRequirementsList( _plugin );
    }

    /**
     * Load the data of all the attributeRequirement objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the attributeRequirement objects
     */
    public static ReferenceList getAttributeRequirementsReferenceList( )
    {
        return _dao.selectAttributeRequirementsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<AttributeRequirement> getAttributeRequirementsListByIds( List<Integer> listIds )
    {
        return _dao.selectAttributeRequirementsListByIds( _plugin, listIds );
    }

}
