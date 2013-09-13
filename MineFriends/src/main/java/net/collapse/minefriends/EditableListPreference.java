package net.collapse.minefriends;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import net.collapse.minefriends.model.StringSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a list of servers which the user can edit.
 * @author David
 */
public abstract class EditableListPreference<D> extends Preference implements EditableListFragment.InvalidationListener<D>
{
	private FragmentManager         fragmentManager;
	private EditableListFragment<D> fragment;

	protected List<D> data = new ArrayList<D>();

	protected StringSerializer<D> serializer;

	public EditableListPreference(Context context)
	{
		super(context);
	}

	public EditableListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public EditableListPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		final Parcelable superState = super.onSaveInstanceState();

		// Check whether this Preference is persistent (continually saved)
		if (isPersistent())
		{
			// No need to save instance state since it's persistent, use superclass state
			return superState;
		}

		final SavedState state = new SavedState(superState);
		state.value = this.dataToString();
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		// Check whether we saved the state in onSaveInstanceState
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save the state, so call superclass
			super.onRestoreInstanceState(state);
			return;
		}

		// Cast state to custom BaseSavedState and pass to superclass
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());

		// Set this Preference's widget to reflect the restored state
		this.data.clear();
		this.stringToData(savedState.value, this.data);
		if(this.fragment != null)
		{
			this.fragment.onDataInvalidation();
		}
	}

	@Override
	protected void onClick()
	{
		super.onClick();

		FragmentUtils.changeContentTo(this.fragment, getFragmentManager());
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		if(restorePersistedValue)
		{
			this.loadData();
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return new ArrayList<D>();
	}

	public FragmentManager getFragmentManager()
	{
		return fragmentManager;
	}

	public void setFragmentManager(FragmentManager fragmentManager)
	{
		this.fragmentManager = fragmentManager;
	}

	public void setFragmentToOpen(EditableListFragment<D> fragment)
	{
		this.fragment = fragment;
		this.fragment.setData(this.data);
		this.fragment.setListener(this);
	}

	public List<D> getData()
	{
		return data;
	}

	public void setSerializer(StringSerializer<D> serializer)
	{
		this.serializer = serializer;
	}

	public String dataToString()
	{
		StringBuilder storage = new StringBuilder();
		for(D d : data)
		{
			if(storage.length() != 0)
			{
				storage.append("||");
			}
			storage.append(serializer.asString(d));
		}
		return storage.toString();
	}

	public void stringToData(String storage, List<D> result)
	{
		String[] parts = storage.split("\\|\\|");
		for(String part : parts)
		{
			D object = serializer.fromString(this.getPersistedString(""));
			if(object != null)
			{
				result.add(object);
			}
		}
	}

	public void saveData()
	{
		String storage = this.dataToString();
		this.persistString(storage.toString());
	}

	public void loadData()
	{
		String storage = this.getPersistedString("");

		this.data.clear();
		this.stringToData(storage, this.data);

		if(this.fragment != null)
		{
			this.fragment.onDataInvalidation();
		}
	}

	@Override
	public void onInvalidation(List<D> data)
	{
		saveData();
	}

	// -----------------------------------------------------------------------------------------------
	// Inner types
	// -----------------------------------------------------------------------------------------------

	private static class SavedState extends BaseSavedState
	{
		private String value;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			value = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(value);
		}

		// Standard creator object using an instance of this class
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
		{
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
