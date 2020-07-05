/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
package fr.paris.lutece.plugins.identitystore.service.search;

import fr.paris.lutece.plugins.identitystore.v2.web.rs.dto.IdentityDto;
import fr.paris.lutece.portal.service.util.AppException;
import java.util.List;
import java.util.Map;

public interface ISearchIdentityService
{
    /**
     * returns a list of identity from combination of attributes
     *
     * @param mapAttributeValues
     *            a map that associates list of values to search for some attributes
     * @param listAttributeKeyNames
     *            a list of attributes to retrieve in identities
     * @param strClientApplicationCode
     *            application code who requested identities
     * @return identity filled according to application rights for user identified by connection id
     */
    List<IdentityDto> getIdentities( Map<String, List<String>> mapAttributeValues, List<String> listAttributeKeyNames, String strClientApplicationCode );

    /**
     * check attached files are present in identity Dto and that attributes to update exist and are writable (or not writable AND unchanged)
     *
     * @param mapAttributeValues
     *            map of attached files
     * @param strClientAppCode
     *            application code to check right
     * @throws AppException
     *             thrown if provided attributes are not valid
     */
    void checkSearchAttributes( Map<String, List<String>> mapAttributeValues, String strClientAppCode ) throws AppException;
}
