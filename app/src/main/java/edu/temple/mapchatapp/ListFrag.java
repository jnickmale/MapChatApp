package edu.temple.mapchatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFrag.OnUpdatePartnersListener} interface
 * to handle interaction events.
 * Use the {@link ListFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFrag extends Fragment {




    ArrayAdapter<String> itemsAdapter;

    private OnUpdatePartnersListener mListener;

    public ListFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFrag newInstance(String param1, String param2) {
        ListFrag fragment = new ListFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getView().findViewById(R.id.partnerList);
        ArrayList<Partner> partners = ((OnUpdatePartnersListener)getActivity()).getPartnersList();
        itemsAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, partners);
        listView.setAdapter(itemsAdapter);
        //ListView listView = (ListView) findViewById(R.id.lvItems);
        //listView.setAdapter(itemsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.updatePartners();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUpdatePartnersListener) {
            mListener = (OnUpdatePartnersListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateAdapter(){
        itemsAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUpdatePartnersListener {
        void updatePartners();
        ArrayList<Partner> getPartnersList();
    }
}
