package com.sibedge.sibedge_test.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sibedge.sibedge_test.Adapters.ServiceRecyclerAdapter;
import com.sibedge.sibedge_test.Model.XmlListRow;
import com.sibedge.sibedge_test.R;
import com.sibedge.sibedge_test.Utility.AsyncResponse;
import com.sibedge.sibedge_test.Utility.GetXMLTask;
import com.sibedge.sibedge_test.Utility.Utility;
import com.sibedge.sibedge_test.Utility.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Sermilion on 05/10/2016.
 */

public class SibEDGE_ServiceFragment extends Fragment implements AsyncResponse {

    private ProgressBar mprogressBar;
    private RecyclerView mRecyclerView;
    private ServiceRecyclerAdapter mAdapter;
    private ArrayList<XmlListRow> mItems = new ArrayList<>();


    @Override
    public void processFinish(Object output) {
        XMLParser parser = new XMLParser();
        String xml = (String) output;
        Document doc = parser.getDomElement(xml);
        NodeList nl = doc.getElementsByTagName(XMLParser.KEY_RESULT);
        NodeList resultNodes = nl.item(0).getChildNodes();
        NodeList quotesNodeList = null;
        for (int i = 0; i < resultNodes.getLength(); i++) {
            if (resultNodes.item(i).getNodeName().equals("quotes")) {
                quotesNodeList = resultNodes.item(i).getChildNodes();
            }
        }

        if (quotesNodeList != null) {
            for (int i = 0; i < quotesNodeList.getLength(); i++) {
                if (quotesNodeList.item(i) instanceof Element) {
                    Element e = (Element) quotesNodeList.item(i);
                    String id = parser.getValue(e, XMLParser.KEY_ID);
                    String date = parser.getValue(e, XMLParser.KEY_DATE);
                    Node textNode = e.getElementsByTagName(XMLParser.KEY_TEXT).item(0);
                    String text = textNode.getChildNodes().item(0).getTextContent();
                    mItems.add(new XmlListRow(Integer.valueOf(id), date, text));
                }
            }
        }
        mAdapter = new ServiceRecyclerAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);
        mprogressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mItems", mItems);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState!=null){
            mItems = savedInstanceState.getParcelableArrayList("mItems");
        }
        if ((mItems != null ? mItems.size() : 0) == 0) {
            GetXMLTask asyncTask = new GetXMLTask();
            asyncTask.setDelegate(this);
            final File xmlFile = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_IMAGE, Environment.DIRECTORY_DOCUMENTS, "sibedge.xml");
            asyncTask.execute(getResources().getString(R.string.xmlURL), xmlFile);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.service_recyclerView);
        mprogressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        if (mItems.size() == 0)
            mprogressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ServiceRecyclerAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

}
