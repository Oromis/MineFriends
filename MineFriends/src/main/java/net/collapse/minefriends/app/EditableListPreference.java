package net.collapse.minefriends.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.v4.view.ViewPager;
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
	private ViewPager               pager;
	private int                     targetPage;
	private EditableListFragment<D> fragment;

	protected StringSerializer<D> serializer;

	private List<D> data = new ArrayList<D>();
	private boolean synced = false;

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
		if(isPersistent())
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
		data.clear();
		this.stringToData(savedState.value, data);
		if(this.fragment != null)
		{
			this.fragment.onDataInvalidation();
		}
	}

	@Override
	protected void onClick()
	{
		super.onClick();

		this.pager.setCurrentItem(this.targetPage, true);
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

	public void setPager(ViewPager pager)
	{
		this.pager = pager;
	}

	public void setTargetPage(int targetPage)
	{
		this.targetPage = targetPage;
	}

	public void setFragment(EditableListFragment<D> fragment)
	{
		this.fragment = fragment;
		this.fragment.setListener(this);

		List<D> data = this.fragment.getData();
		if(data != this.data)   // Intentional reference equality check
		{
			data.addAll(this.data);
		}
		this.data = data;
	}

	public String dataToString()
	{
		StringBuilder storage = new StringBuilder();
		for(D d : this.data)
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
			D object = serializer.fromString(part);
			if(object != null)
			{
				result.add(object);
			}
		}
	}

	public void saveData()
	{
		String storage = this.dataToString();
		this.persistString(storage);
	}

	public void loadData()
	{
		String storage = this.getPersistedString("");

		data.clear();
		this.stringToData(storage, data);

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
