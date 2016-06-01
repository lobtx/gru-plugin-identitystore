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
package fr.paris.lutece.plugins.identitystore.web.rs;

import fr.paris.lutece.plugins.identitystore.business.Attribute;
import fr.paris.lutece.plugins.identitystore.business.AttributeKeyHome;
import fr.paris.lutece.plugins.identitystore.business.AttributeRight;
import fr.paris.lutece.plugins.identitystore.business.ClientApplicationHome;
import fr.paris.lutece.plugins.identitystore.business.Identity;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * class to help managing rest feature
 *
 */
public final class IdentityDtoUtil
{
    /**
     * private constructor
     */
    private IdentityDtoUtil(  )
    {
    }

    /**
     * returns a identityDto initialized from provided identity
     * @param identity business identity to convert
     * @param strClientAppCode client app code
     * @return identityDto initialized from provided identity
     */
    public static IdentityDto convertToDto( Identity identity, String strClientAppCode )
    {
        IdentityDto identityDto = new IdentityDto(  );
        identityDto.setConnectionId( identity.getConnectionId(  ) );
        identityDto.setCustomerId( identity.getCustomerId(  ) );

        if ( identity.getAttributes(  ) != null )
        {
            Map<String, AttributeDto> mapAttributeDto = new HashMap<String, AttributeDto>(  );
            List<AttributeRight> lstRights = ClientApplicationHome.selectApplicationRights( ClientApplicationHome.findByCode( 
                        strClientAppCode ) );

            for ( Attribute attribute : identity.getAttributes(  ).values(  ) )
            {
                AttributeDto attrDto = new AttributeDto(  );
                attrDto.setKey( attribute.getKey(  ) );
                attrDto.setValue( attribute.getValue(  ) );
                attrDto.setType( AttributeKeyHome.findByKey( attribute.getKey(  ) ).getKeyType(  ).getCode(  ) );

                for ( AttributeRight attRight : lstRights )
                {
                    if ( attRight.getAttributeKey(  ).getKeyName(  ).equals( attribute.getKey(  ) ) )
                    {
                        attrDto.setReadable( attRight.isReadable(  ) );
                        attrDto.setWritable( attRight.isWritable(  ) );
                        attrDto.setCertifiable( attRight.isCertifiable(  ) );
                        attrDto.setCertified( attribute.getCertificate(  ) != null );

                        break;
                    }
                }

                mapAttributeDto.put( attrDto.getKey(  ), attrDto );
            }

            identityDto.setAttributes( mapAttributeDto );
        }

        return identityDto;
    }
}
