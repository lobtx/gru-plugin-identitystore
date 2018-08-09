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
package fr.paris.lutece.plugins.identitystore.service;

import java.util.HashMap;

import fr.paris.lutece.plugins.identitystore.IdentityStoreTestContext;
import fr.paris.lutece.plugins.identitystore.business.AttributeCertificate;
import static fr.paris.lutece.plugins.identitystore.business.AttributeCertificateUtil.createAttributeCertificateInDatabase;
import static fr.paris.lutece.plugins.identitystore.business.AttributeCertificateUtil.createExpiredAttributeCertificateInDatabase;
import fr.paris.lutece.plugins.identitystore.business.AttributeKey;
import fr.paris.lutece.plugins.identitystore.business.AttributeKeyHome;
import fr.paris.lutece.plugins.identitystore.business.Identity;
import fr.paris.lutece.plugins.identitystore.business.IdentityAttribute;
import static fr.paris.lutece.plugins.identitystore.business.IdentityAttributeUtil.createIdentityAttributeInDatabase;
import static fr.paris.lutece.plugins.identitystore.business.IdentityUtil.createIdentityInDatabase;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeStatusDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.CertificateDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityChangeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.MockAttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.MockCertificateDto;
import static fr.paris.lutece.plugins.identitystore.web.rs.dto.MockIdentityChangeDto.createIdentityChangeDtoFor;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.MockIdentityDto;
import fr.paris.lutece.test.LuteceTestCase;

/**
 *
 * @author levy
 */
public class IdentityStoreServiceUpdateIdentityTest extends LuteceTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        IdentityStoreTestContext.initContext( );
    }

    public void testUpdateIdentityWithApplicationNotAllowedToRemoveValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithApplicationNotAllowedToRemoveValue" );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( null );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithApplicationNotAllowedToRemoveValue", attr1After.getValue( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_DELETE_NOT_ALLOW_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithNoAttributeInDatabaseAndNoCertificateInInput( )
    {
        Identity identityReference = createIdentityInDatabase( );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        MockAttributeDto.create( identityDto, IdentityStoreTestContext.ATTRKEY_1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithNoAttributeInDatabaseAndNoCertificateInInput" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndNoCertificateInInput", attr1After.getValue( ) );
        assertNull( attr1After.getCertificate( ) );
        assertFalse( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndNoCertificateInInput", attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInput( )
    {
        Identity identityReference = createIdentityInDatabase( );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, IdentityStoreTestContext.ATTRKEY_1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInput" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInput", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInput", attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1StatusAfter.getNewCertifier( ), attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInputWithNoExpirationDate( )
    {
        Identity identityReference = createIdentityInDatabase( );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, IdentityStoreTestContext.ATTRKEY_1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInputWithNoExpirationDate" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER4_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInputWithNoExpirationDate",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoAttributeInDatabaseAndCertificateInInputWithNoExpirationDate",
                attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1StatusAfter.getNewCertifier( ), attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithCertificateInDatabaseButNotInInputAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithCertificateInDatabaseButNotInInputAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        attr1.setCertificate( null );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificateInDatabaseButNotInInputAndSameValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithCertificateInDatabaseButNotInInputAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithCertificateInDatabaseButNotInInputAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        attr1.setCertificate( null );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificateInDatabaseButNotInInputAndNewValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithLowerLevelCertificateAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithLowerLevelCertificateAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithLowerLevelCertificateAndSameValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithLowerLevelCertificateAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithLowerLevelCertificateAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithLowerLevelCertificateAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithLowerLevelCertificateAndNewValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithHigherLevelCertificateAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithHigherLevelCertificateAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithHigherLevelCertificateAndSameValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithHigherLevelCertificateAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithHigherLevelCertificateAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithHigherLevelCertificateAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithHigherLevelCertificateAndNewValue_newValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithHigherLevelCertificateAndNewValue_newValue", attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER3_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_LONGER_CERTIFIER_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER3_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateSoonerAndNewValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_LONGER_CERTIFIER_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER3_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER3_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndNewValue_newValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndExpirationDateLaterAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER2_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER4_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER4_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER2_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndNewValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER4_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithHigherLevelCertificateAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER4_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndNewValue_newValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInRequestAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndInRequestAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER4_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER5_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndInRequestAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER4_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndInRequestAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER5_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithSameLevelCertificateAndNoExpirationDateInDatabaseAndInRequestAndNewValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER4_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_VALUE_CERTIFIED_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createExpiredAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndSameValue",
                attr1After.getValue( ) );
        assertNull( attr1After.getCertificate( ) );
        assertFalse( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_NO_CHANGE_REQUEST_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createExpiredAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER1_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndNewValue_newValue",
                attr1After.getValue( ) );
        assertNull( attr1After.getCertificate( ) );
        assertFalse( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndNoCertificateInInputAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createExpiredAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndSameValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndSameValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndSameValue",
                attr1StatusAfter.getNewValue( ) );
        assertNull( IdentityStoreTestContext.CERTIFIER1_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeCertificate attributeCertificate = createExpiredAttributeCertificateInDatabase( IdentityStoreTestContext.CERTIFIER2_CODE );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndNewValue", attributeCertificate );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndNewValue_newValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithExpiredCertificateInDatabaseAndCertificateInInputAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertNull( IdentityStoreTestContext.CERTIFIER1_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndSameValue" );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndSameValue", attr1After.getValue( ) );
        assertNull( attr1After.getCertificate( ) );
        assertFalse( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.INFO_NO_CHANGE_REQUEST_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithSameValueAndNoCertificateInRequestNorDatabase" );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndNewValue_newValue", attr1After.getValue( ) );
        assertNull( attr1After.getCertificate( ) );
        assertFalse( attr1After.isCertified( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithNoCertificateInDatabaseNorInInputAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertNull( attr1StatusAfter.getNewCertifier( ) );
        assertNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndSameValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndSameValue" );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndSameValue", attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertNull( attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    public void testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndNewValue( )
    {
        Identity identityReference = createIdentityInDatabase( );
        AttributeKey attributeKey1 = findAttributeKey( IdentityStoreTestContext.ATTRKEY_1 );
        IdentityAttribute identityAttribute1 = createIdentityAttributeInDatabase( identityReference, attributeKey1, IdentityStoreTestContext.ATTRKEY_1
                + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndNewValue" );
        IdentityDto identityDto = MockIdentityDto.create( identityReference );
        AttributeDto attr1 = MockAttributeDto.create( identityDto, identityAttribute1 );
        attr1.setValue( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndNewValue_newValue" );
        IdentityChangeDto identityChangeDto = createIdentityChangeDtoFor( identityDto );
        CertificateDto certificateDto = MockCertificateDto.create( IdentityStoreTestContext.CERTIFIER1_CODE );
        attr1.setCertificate( certificateDto );

        IdentityDto identityAfterDto = IdentityStoreService.updateIdentity( identityChangeDto, new HashMap<>( ) );

        assertNotNull( identityAfterDto );
        assertNotNull( identityAfterDto.getAttributes( ) );
        assertEquals( 1, identityAfterDto.getAttributes( ).size( ) );
        AttributeDto attr1After = identityAfterDto.getAttributes( ).get( IdentityStoreTestContext.ATTRKEY_1 );
        assertNotNull( attr1After );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndNewValue_newValue",
                attr1After.getValue( ) );
        assertNotNull( attr1After.getCertificate( ) );
        assertTrue( attr1After.isCertified( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1After.getCertificate( ).getCertifierCode( ) );
        AttributeStatusDto attr1StatusAfter = attr1After.getStatus( );
        assertNotNull( attr1StatusAfter );
        assertEquals( AttributeStatusDto.OK_CODE, attr1StatusAfter.getStatusCode( ) );
        assertEquals( IdentityStoreTestContext.ATTRKEY_1 + "testUpdateIdentityWithCertificatetNotInDatabaseButInInputAndNewValue_newValue",
                attr1StatusAfter.getNewValue( ) );
        assertEquals( IdentityStoreTestContext.CERTIFIER1_CODE, attr1StatusAfter.getNewCertifier( ) );
        assertNotNull( attr1StatusAfter.getNewCertificateExpirationDate( ) );
    }

    // ###########################
    // ##### Utility methods #####
    // ###########################

    private AttributeKey findAttributeKey( String strAttributeKeyName )
    {
        return AttributeKeyHome.findByKey( strAttributeKeyName );
    }
}
