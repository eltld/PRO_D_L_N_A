package com.wireme.mediaserver;

import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

import android.util.Log;

public class ContentDirectoryService extends AbstractContentDirectoryService {
    // private final static String LOGTAG = "MediaServer-CDS";
    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby) throws ContentDirectoryException {
        try {
            DIDLContent didl = new DIDLContent( );
            ContentNode contentNode = ContentTree.getNode( objectID );
            // Log.e( "hai", "someone's browsing id: " + objectID );
            if (contentNode == null)
                return new BrowseResult( "", 0, 0 );
            if (contentNode.isItem( )) {
                didl.addItem( contentNode.getItem( ) );
                // MyLog.e( LOGTAG, "returing item: " + contentNode.getItem( ).getTitle( ) );
                return new BrowseResult( new DIDLParser( ).generate( didl ), 1, 1 );
            } else {
                if (browseFlag == BrowseFlag.METADATA) {
                    didl.addContainer( contentNode.getContainer( ) );
                    // MyLog.e( LOGTAG, "returning metadata of container: " + contentNode.getContainer( ).getTitle( ) );
                    return new BrowseResult( new DIDLParser( ).generate( didl ), 1, 1 );
                } else {
                    int num = 0;
                    if (maxResults <= 0)
                        maxResults = contentNode.getContainer( ).getChildCount( );
                    for (Container container : contentNode.getContainer( ).getContainers( )) {
                        didl.addContainer( container );
                        num++;
                        if (num > maxResults)
                            break;
                        // MyLog.e( LOGTAG, "------------------getting child container: " + container.getTitle( ) );
                    }
                    for (Item item : contentNode.getContainer( ).getItems( )) {
                        didl.addItem( item );
                        num++;
                        if (num > maxResults)
                            break;
                        // MyLog.e( LOGTAG, "--------------getting child item: " + item.getTitle( ) );
                    }
                    // MyLog.e( LOGTAG, "--------------getting child firstResult: " + firstResult + "   maxResults=" + maxResults + "  num=" + num );
                    return new BrowseResult( new DIDLParser( ).generate( didl ), contentNode.getContainer( ).getChildCount( ), contentNode.getContainer( ).getChildCount( ) );
                }
            }
        } catch (Exception ex) {
            throw new ContentDirectoryException( ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString( ) );
        }
    }

    /**
     * 实现对本地搜索
     */
    @Override
    public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy)
            throws ContentDirectoryException {
        // Log.e( "hai", "-----------searchsearchsearchsearchsearchsearch------" );
        Log.e( "hai", "-----------searchsearchsearchsearchsearchsearch------" );
        Log.e( "hai", "-----------searchsearchsearchsearchsearchsearch------" );
        // You can override this method to implement searching!
        return this.browse( containerId, BrowseFlag.METADATA, filter, firstResult, maxResults, orderBy );
        // return this.search( containerId, searchCriteria, filter, firstResult, maxResults, orderBy );

        // You can override this method to implement searching!
        // return super.search( containerId, searchCriteria, filter, firstResult, maxResults, orderBy );
    }
}
