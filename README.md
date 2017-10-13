![](http://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=gru-plugin-identitystore-deploy)
# Plugin Identity Store

![](http://dev.lutece.paris.fr/plugins/plugin-identitystore/images/identitystore.png)

## Introduction

This plugin stores identities. An identity is composed of attributes. Each attribute can be read, written or certified by the application calling the service.

## Encryption of identities

The plugin can use identities with encrypted ids. This mechanism permits to have a specific id for each service provider. Thus, the id is not shared.

To use encryption, the site has to contain a class implementing the interface `fr.paris.lutece.plugins.identitystore.service.encryption.IIdentityEncryptionService` from the library `gru-library-identitystore` . For example, add the plugin `gru-plugin-grukeydiversification` in the `pom.xml` of the site. The encryption is then enabled.

## Configuration

The bean names `identitystore.identityInfoExternalProvider` must be defined using Spring configuration. The bean must implement the Java interface `fr.paris.lutece.plugins.identitystore.service.external.IIdentityInfoExternalProvider` . It is used to create an identity from an external source when a connection id is provided in the web method *create* .

## Usage

The plugins exposes web methods to manage identities. Check */jsp/site/Portal.jsp?page=swaggerui* page for more details about these methods.


[Maven documentation and reports](http://dev.lutece.paris.fr/plugins/plugin-identitystore/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*