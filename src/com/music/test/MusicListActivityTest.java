package com.music.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.music.MyMusicPlayerActivity;
import com.music.PlaylistMusicActivity;
import com.music.R;
import com.music.list.MusicListActivity;
import com.music.list.adapter.MusicListAdapter;
import com.music.parts.RandomButton;
import com.music.pojo.Music;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class MusicListActivityTest extends
		ActivityInstrumentationTestCase2<MusicListActivity> {

	public MusicListActivityTest() {
		super(MusicListActivity.class);
	}

	private Solo solo;
	private int height;
	private int width;
	private Music songTobeCenter;
	private int counter;
	private MyMusicPlayerActivity mActivity;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		DisplayMetrics displaymetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

	}

	public void testOnePlaylistToAnother() {

	}

	public void testSelectUnderbarShuffle() {
		solo.assertCurrentActivity("testmes", MusicListActivity.class);

		MusicListActivity mActivity = (MusicListActivity) solo
				.getCurrentActivity();

	}

	private static final int TO_ALL_LIST = -1;
	private static final boolean IS_SHUFFLE = true;
	private static final boolean ON_UNDERBAR = true;
	private static final boolean NOT_SHUFFLE = false;
	private static final boolean NOT_UNDERBAR = false;
	private ArrayList<Music> listToTest;

	public void testTransition1() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, 0, NOT_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition2() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, 1, NOT_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition3() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, 0, IS_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition4() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, 1, IS_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition5() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, TO_ALL_LIST,
				NOT_SHUFFLE, ON_UNDERBAR, 0, 0);
	}

	public void testTransition7() {
		selectAllListToAnotherListInner(false, 0, 2, 5, NOT_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition8() {
		selectAllListToAnotherListInner(false, 0, 2, 5, IS_SHUFFLE,
				NOT_UNDERBAR, 0, 0);
	}

	public void testTransition9() {
		selectAllListToAnotherListInner(true, 0, TO_ALL_LIST, 4, NOT_SHUFFLE,
				NOT_UNDERBAR, 3, 3);
	}
	public void testTransition10() {
		selectAllListToAnotherListInner(true, 0, 2, 2, NOT_SHUFFLE,
				NOT_UNDERBAR, 3, 3);
	}
	public void testTransition11() {
		selectAllListToAnotherListInner(true, 0, 2, 2, IS_SHUFFLE,
				NOT_UNDERBAR, 3, 3);
	}
	public void testTransition12() {
		selectAllListToAnotherListInner(false, 0, 2, 2, NOT_SHUFFLE,
				NOT_UNDERBAR, 3, 3);
	}
	public void testTransition13() {
		selectAllListToAnotherListInner(false, 0, 2, 2, IS_SHUFFLE,
				NOT_UNDERBAR, 3, 3);
	}

	private void selectAllListToAnotherListInner(boolean toAllList,
			int fromChildNum, int toListNum, int toChildNum, boolean shuffle,
			boolean onUnderbar, int fromSwipeNum, int toSwipeNum) {

		solo.assertCurrentActivity("testmes", MusicListActivity.class);

		ArrayList<ListView> mainList = solo.getCurrentViews(ListView.class);
		assertTrue(mainList != null && mainList.get(0) != null);

		ListView list = mainList.get(1);
		final MusicListAdapter adapter = (MusicListAdapter) list.getAdapter();
		assertNotNull(adapter);

		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				int count = adapter.getCount();
				return count > 0;
			}
		}, 2000);

		ArrayList<TextView> textDataFrom = solo.clickInList(fromChildNum, 1);
		String fromText = textDataFrom.get(0).getText().toString();

		// ~~~~MyMusicPlayerActivity.class~~~~

		assertTrue(solo.waitForActivity(MyMusicPlayerActivity.class));

		mActivity = (MyMusicPlayerActivity) solo.getCurrentActivity();

		solo.sleep(2000);

		ListView listViewCenter = (ListView) mActivity.playerContainers.get(1)
				.findViewById(R.id.info);
		TextView obj = (TextView) listViewCenter.getChildAt(0);

		assertEquals(obj.getText(), fromText);

		SharedPreferences pushState = mActivity.getSharedPreferences(
				"randomBtn:pushState", Activity.MODE_PRIVATE);
		Boolean state = pushState.getBoolean("pushState", false);

		if (shuffle != state) {
			List<RandomButton> btnlist = solo.getCurrentViews(
					RandomButton.class,
					(LinearLayout) mActivity.playerContainers.get(1));
			assertNotNull(list);
			assertEquals(1, btnlist.size());

			solo.clickOnView(btnlist.get(0));
			solo.sleep(200);
		}

		assertEquals(fromText, obj.getText().toString());

		listToTest = new ArrayList<Music>(
				mActivity.playlist);
		if (shuffle) {
			counter = 0;
		} else {
			counter = fromChildNum;
		}

		if (fromSwipeNum > 0) {
			for (int i = 0; i < fromSwipeNum; i++) {
				if (counter != listToTest.size() - 1) {
					counter = counter + 1;
					songTobeCenter = listToTest.get(counter);
				} else {
					counter = 0;
					songTobeCenter = listToTest.get(0);
				}
				solo.drag(width - 20, 0, height - 20, height - 20, 10);
				solo.sleep(2000);

				assertEquals(mActivity.order, counter);
				listViewCenter = (ListView) mActivity.playerContainers.get(1)
						.findViewById(R.id.info);
				obj = (TextView) listViewCenter.getChildAt(0);
				assertEquals(obj.getText(), songTobeCenter.getTitle());

			}

		}

		solo.goBack();

		// ~~~~ MusicList ~~~~

		assertTrue(solo.waitForActivity(MusicListActivity.class));
		TextView statusBar = (TextView) solo.getView(com.music.R.id.status);

		solo.sleep(200);

		String toText = null;
		ArrayList<TextView> textDataTo = null;
		if (onUnderbar) {
			assertNotNull(statusBar);
			assertNotNull(statusBar.getText());

			assertTrue(statusBar.getText().toString().contains(fromText));
			textDataTo = textDataFrom;
			solo.clickOnView(statusBar);

		} else if (toAllList) {
			textDataTo = solo.clickInList(toChildNum, 1);
		} else {
			solo.drag(width - 20, 0, height / 2, height / 2, 40);
			solo.clickInList(toListNum, 0);

			solo.sleep(200);
			
			assertTrue(solo.waitForActivity(PlaylistMusicActivity.class));
			

			textDataTo = solo.clickInList(toChildNum);

		}

		toText = textDataTo.get(0).getText().toString();

		// MyMusicPlayer again

		assertTrue(solo.waitForActivity(MyMusicPlayerActivity.class));

		mActivity = (MyMusicPlayerActivity) solo.getCurrentActivity();
		assertNotNull(mActivity);

		solo.sleep(200);

		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				return mActivity.playlist != null
						&& mActivity.playlist.size() > 0;
			}
		}, 200);

		solo.sleep(2000);
		
		listToTest = new ArrayList<Music>(
				mActivity.playlist);

		listViewCenter = (ListView) mActivity.playerContainers.get(1)
				.findViewById(R.id.info);
		obj = (TextView) listViewCenter.getChildAt(0);

		assertEquals(toText, obj.getText().toString());

		if (shuffle) {
			counter = 0;
		} else {
			counter = toChildNum - 1;
		}

		if (toSwipeNum > 0) {
			for (int i = 0; i < toSwipeNum; i++) {
				if (counter != listToTest.size() - 1) {
					counter = counter + 1;
					songTobeCenter = listToTest.get(counter);
				} else {
					counter = 0;
					songTobeCenter = listToTest.get(0);
				}
				solo.drag(width - 20, 0, height - 20, height - 20, 10);
				solo.sleep(2000);

				assertEquals(mActivity.order, counter);
				listViewCenter = (ListView) mActivity.playerContainers.get(1)
						.findViewById(R.id.info);
				obj = (TextView) listViewCenter.getChildAt(0);
				assertEquals(obj.getText(), songTobeCenter.getTitle());

			}

		}

	}

	/**
	 * select one music in list and after going back check if song status
	 * updates
	 * 
	 * @throws InterruptedException
	 */
	public void testSelectOneMusic() throws InterruptedException {

		solo.assertCurrentActivity("testmes", MusicListActivity.class);
		solo.assertMemoryNotLow();
		ArrayList<ListView> mainList = solo.getCurrentViews(ListView.class);
		assertTrue(mainList != null && mainList.get(0) != null);

		ListView list = mainList.get(1);
		final MusicListAdapter adapter = (MusicListAdapter) list.getAdapter();
		assertNotNull(adapter);

		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				int count = adapter.getCount();
				return count > 0;
			}
		}, 2000);

		Music music = adapter.getItem(0);
		assertNotNull(music.getTitle());

		ArrayList<TextView> textViews = solo.clickInList(0);
		textViews = solo.clickInList(0, 1);
		solo.waitForText(music.getTitle());
		assertTrue(solo.waitForActivity(MyMusicPlayerActivity.class));
		solo.goBack();

		assertTrue(solo.waitForActivity(MusicListActivity.class));
		TextView statusBar = (TextView) solo.getView(com.music.R.id.status);

		solo.sleep(200);

		assertNotNull(statusBar);
		assertNotNull(statusBar.getText());

		assertTrue(statusBar.getText().toString().contains(music.getTitle()));
	}

	/**
	 * Test if loaded playlist correspond to current song title text in
	 * viewpager. Precondition : you have just TWO songs in play list to test
	 * !!!
	 */
	public void testSwipeInPlayerTwoSongsRight() {
		playlistSwipeCheck(3, 2, true, false);

	}

	public void testSwipeInPlayerTwoSongsLeft() {
		playlistSwipeCheck(3, 2, false, false);

	}

	public void testSwipeInPlayerTwoSongsRightShuffle() {
		playlistSwipeCheck(3, 2, true, true);

	}

	public void testSwipeInPlayerTwoSongsLeftShuffle() {
		playlistSwipeCheck(3, 2, false, true);

	}

	public void testSwipeInPlayerOneSongRight() {
		playlistSwipeCheck(5, 1, false, false);

	}

	public void testSwipeInPlayerOneSongLeft() {
		playlistSwipeCheck(5, 1, true, false);

	}

	public void testSwipeInPlayerOneSongRightShuffle() {
		playlistSwipeCheck(5, 1, false, true);

	}

	public void testSwipeInPlayerOneSongLeftShuffle() {
		playlistSwipeCheck(5, 1, true, true);

	}

	private void playlistSwipeCheck(int listnum, final int size,
			boolean toLeft, boolean shuffle) {

		int PLAYLIST_NUM = listnum;

		solo.assertCurrentActivity("testmes", MusicListActivity.class);
		solo.assertMemoryNotLow();

		solo.sleep(200);
		solo.drag(width - 20, 0, height / 2, height / 2, 10);

		solo.clickInList(PLAYLIST_NUM, 0);
		assertTrue(solo.waitForActivity(PlaylistMusicActivity.class));
		solo.sleep(200);
		solo.clickInList(0);
		assertTrue(solo.waitForActivity(MyMusicPlayerActivity.class));

		ArrayList<ListView> listviews = solo.getCurrentViews(ListView.class);

		solo.assertCurrentActivity("test", MyMusicPlayerActivity.class);
		final MyMusicPlayerActivity mActivity = (MyMusicPlayerActivity) solo
				.getCurrentActivity();
		assertNotNull(mActivity);

		solo.sleep(200);

		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				return mActivity.playlist != null
						&& mActivity.playlist.size() >= size;
			}
		}, 200);

		SharedPreferences pushState = mActivity.getSharedPreferences(
				"randomBtn:pushState", Activity.MODE_PRIVATE);
		Boolean state = pushState.getBoolean("pushState", false);

		List<RandomButton> list = solo.getCurrentViews(RandomButton.class,
				(LinearLayout) mActivity.playerContainers.get(1));
		assertNotNull(list);
		assertEquals(1, list.size());

		if (shuffle != state) {
			solo.clickOnView(list.get(0));
			solo.sleep(200);
		}

		final ArrayList<Music> listToTest = new ArrayList<Music>(
				mActivity.playlist);

		int i = 0;
		int cnt = 0;
		while (cnt < size) {

			if (toLeft) {
				solo.drag(0 + 20, width, height - 20, height - 20, 10);
			} else {
				solo.drag(width - 20, 0, height - 20, height - 20, 10);
			}

			solo.sleep(200);

			if (size == 1) {
				songTobeCenter = listToTest.get(0);
				counter = 0;
			} else if (toLeft) {
				if (i != 1) {
					counter = i == 0 ? size - 1 : i - 1;
					songTobeCenter = listToTest.get(counter);
				} else {
					counter = 0;
					songTobeCenter = listToTest.get(0);
				}

			} else {
				if (i != size - 1) {
					counter = i + 1;
					songTobeCenter = listToTest.get(counter);
				} else {
					counter = 0;
					songTobeCenter = listToTest.get(0);
				}

			}

			solo.sleep(1000);

			assertEquals(mActivity.order, counter);
			assertTrue(mActivity.playlist != null);
			assertEquals(mActivity.playlist.get(mActivity.order).getTitle(),
					songTobeCenter.getTitle());

			ListView listViewCenter = (ListView) mActivity.playerContainers
					.get(1).findViewById(R.id.info);
			TextView obj = (TextView) listViewCenter.getChildAt(0);
			assertTrue(obj.getText() != null
					&& obj.getText().equals(songTobeCenter.getTitle()));
			assertEquals(obj.getText(), songTobeCenter.getTitle());

			if (toLeft) {
				i = i == 0 ? size - 1 : i - 1;
			} else {
				i = i == size - 1 ? 0 : i + 1;
			}
			cnt++;

		}

	}

	/**
	 * Test if loaded playlist corresponds to current song title text in
	 * viewpager. Precondition : you have just THREE songs in play list to test
	 * !!!
	 */
	public void testSwipeInPlayerThreeSongsRight() {
		playlistSwipeCheck(4, 3, true, false);

	}

	public void testSwipeInPlayerThreeSongsLeft() {

		playlistSwipeCheck(4, 3, false, false);

	}

	public void testSwipeInPlayerThreeSongsRightShuffle() {
		playlistSwipeCheck(4, 3, true, true);

	}

	public void testSwipeInPlayerThreeSongsLeftShuffle() {

		playlistSwipeCheck(4, 3, false, true);

	}

	@Override
	protected void tearDown() throws Exception {
		// solo.
		solo.finishOpenedActivities();
	}

}
