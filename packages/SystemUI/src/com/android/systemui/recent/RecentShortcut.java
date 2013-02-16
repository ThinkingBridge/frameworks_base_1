package com.android.systemui.recent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.Pair;
import java.util.Arrays;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import com.android.systemui.R;

public class RecentShortcut extends Activity {
    private final ArrayList<ResolveInfo> mPackageInfoList = new ArrayList<ResolveInfo>();
    private final ArrayList<String> stapplist = new ArrayList<String>();
    Dialog addapp;
    public int complete;
    Appload appload;
    int n;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        refresh();
		appload = new Appload();
		appload.run();
	}
	public void refresh(){
		stapplist.clear();
		setContentView(R.layout.recentshortcut);
		n = 0;
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        for(;;){
        	n = n+1;
        String z = pref.getString("app"+String.valueOf(n), "");
        if(z.length() == 0){
        break;
        }else{
        	stapplist.add(z);
        }
        if(stapplist.size() > 0){
        	TextView t1 = (TextView) findViewById(R.id.t1);
        	TextView t2 = (TextView) findViewById(R.id.t2);
        	t1.setVisibility(View.GONE);
        	t2.setVisibility(View.GONE);
        	ListView al = (ListView)findViewById(R.id.applist);
	        MyAdapter2 adapter2 = new MyAdapter2(this, R.layout.listapp, stapplist);
        	al.setAdapter(adapter2);
        }
        }
	}
    public void onStop(){
      super.onStop();
      restartSystemUI();
    }
    public void restartSystemUI() {
        new CMDProcessor().su.run("pkill -TERM -f com.android.systemui");
    }
    public void Toast(String msg){
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recentsh,menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
  		switch(item.getItemId()){
  		case R.id.addapp:
  			for(;;){
  			if(complete == 1){
  			addapp = new Dialog(this);
		    addapp.setContentView(R.layout.addapp);
		    addapp.setTitle(getString(R.string.chooseapp));
	        MyAdapter adapter = new MyAdapter(this, R.layout.selectapp, mPackageInfoList);
	        ListView lv = (ListView) addapp.findViewById(R.id.sapplist);
	        lv.setAdapter(adapter);
	        lv.setOnItemClickListener(new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	long id) {
	ResolveInfo temp = mPackageInfoList.get(position);
	String z1 = temp.activityInfo.packageName;
	int num = 0;
	for(;;){
		if(stapplist.size() == num){
			break;
		}else{
			if(z1.equals(stapplist.get(num))){
				Toast("");
				return;
			}else{
				num = num+1;
			}
		}
	}
    stapplist.add(z1);
	SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
	SharedPreferences.Editor editor = pref.edit(); 
	editor.putString("app"+stapplist.size(), z1);
	editor.commit();
    addapp.dismiss();
    refresh();
	}


	    });
	        addapp.setOwnerActivity(this);
		    addapp.show();
		    break;
  		}else{
  			
  		}
  	    break;
	}
  		}
  		return true;
	}
	 public class MyAdapter2 extends ArrayAdapter<String> {

	     List<String> child;
	     
	public MyAdapter2(Context context, int textViewResourceId, List<String> objects) {
	super(context, textViewResourceId, objects);
	child = objects;
	}
	@Override
	public int getCount() {
	// TODO Auto-generated method stub
	return child.size();
	}
	@Override
	public String getItem(int position) {
	// TODO Auto-generated method stub
	return child.get(position);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		   if(convertView == null) {
			    LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			    convertView = li.inflate(R.layout.listapp, null);
			   }
			   ImageView icon = (ImageView) convertView.findViewById(R.id.appicon);
			   TextView name = (TextView) convertView.findViewById(R.id.appname);
			   
			   String temp = child.get(position);
			   icon.setImageDrawable(loadappicon(temp));
			   
			   name.setText(loadappname(temp));
			   Button delete = (Button) convertView.findViewById(R.id.delete);
			   delete.setOnClickListener(new View.OnClickListener() { 
		            public void onClick(View v) { 
		            	stapplist.remove(child.get(position));
		            	SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		            	SharedPreferences.Editor editor = pref.edit(); 
		            	editor.clear();
		            	int number = 0;
		            	for(;;){
		            		if(stapplist.size() == number){
		            		break;
		            		}else{
		            			int number2 = number+1;
		            			editor.putString("app"+number2, stapplist.get(number).toString());
		            			number = number+1;
		            		}
		            	}
		            	editor.commit();
		            	refresh();
		               } 
		           }); 		   
			  
			   return convertView;
	}
	 }
	 public class MyAdapter extends ArrayAdapter<ResolveInfo> {

	     List<ResolveInfo> child;
	     
	public MyAdapter(Context context, int textViewResourceId, List<ResolveInfo> objects) {
	super(context, textViewResourceId, objects);
	child = objects;
	}
	@Override
	public int getCount() {
	// TODO Auto-generated method stub
	return child.size();
	}
	@Override
	public ResolveInfo getItem(int position) {
	// TODO Auto-generated method stub
	return child.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		   if(convertView == null) {
			    LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			    convertView = li.inflate(R.layout.selectapp, null);
			   }
			   ImageView icon = (ImageView) convertView.findViewById(R.id.sappicon);
			   TextView name = (TextView) convertView.findViewById(R.id.sappname);
			   
			   ResolveInfo temp = child.get(position);
			   if(temp.loadIcon(getPackageManager()) == null){
				   stapplist.remove(child.get(position));
				   refresh();
			   }else{
			   icon.setImageDrawable(temp.loadIcon(getPackageManager()));
			   }
			   
			   name.setText(temp.loadLabel(getPackageManager()));
			   
			  
			   return convertView;
	}
	 }
	 
	 private final static Comparator<ResolveInfo> sDisplayNameComparator
	    = new Comparator<ResolveInfo>() {
	public final int
	compare(ResolveInfo a, ResolveInfo b) {
	    return collator.compare(a.resolvePackageName, b.resolvePackageName);
	}
	private final Collator collator = Collator.getInstance();
	};
	public class Appload extends Thread{
		public void run(){
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	        final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
	        for (int i=0; i<pkgAppsList.size(); i++) {
	            ResolveInfo ai = pkgAppsList.get(i);
	            // On a user build, we only allow debugging of apps that
	            // are marked as debuggable.  Otherwise (for platform development)
	            // we allow all apps.
	            ResolveInfo info = new ResolveInfo();
	            info = ai;
	            info.resolvePackageName = info.loadLabel(getPackageManager()).toString();
	            info.nonLocalizedLabel = (String) (info.loadLabel(getPackageManager()));
	            mPackageInfoList.add(info);
	        }
	        Collections.sort(mPackageInfoList, sDisplayNameComparator);
	        complete = 1;
		}
	}
	public String loadappname(String packagename){
		final PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo( packagename, 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
		return applicationName;
	}
	public Drawable loadappicon(String packagename){
		PackageManager pm = getApplicationContext().getPackageManager();
		Drawable icon = null;
		try {
			icon = pm.getApplicationIcon(packagename);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return icon;
	}
       public class CMDProcessor {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = false;
    private Boolean can_su;
    public SH sh;
    public SH su;

    public CMDProcessor() {
        sh = new SH("sh");
        su = new SH("su");
    }

    public SH suOrSH() {
        return canSU() ? su : sh;
    }

    public boolean canSU() {
        return canSU(false);
    }

    public class CommandResult {
        private final String resultTag = TAG + '.' + getClass().getSimpleName();
        public final String stdout;
        public final String stderr;
        public final Integer exit_value;

        CommandResult(final Integer exit_value_in) {
            this(exit_value_in, null, null);
        }

        CommandResult(final Integer exit_value_in, final String stdout_in,
                final String stderr_in) {
            exit_value = exit_value_in;
            stdout = stdout_in;
            stderr = stderr_in;
            if (DEBUG)
                Log.d(TAG, resultTag + "( exit_value=" + exit_value
                    + ", stdout=" + stdout
                    + ", stderr=" + stderr + " )");
        }

        public boolean success() {
            return exit_value != null && exit_value == 0;
        }

        public EasyPair<String, String> getOutput() {
            return new EasyPair<String, String>(stdout, stderr);
        }
    }

    public class SH {
        private String SHELL = "sh";

        public SH(final String SHELL_in) {
            SHELL = SHELL_in;
        }

        private String getStreamLines(final InputStream is) {
            String out = null;
            StringBuffer buffer = null;
            final DataInputStream dis = new DataInputStream(is);
            try {
                if (dis.available() > 0) {
                    buffer = new StringBuffer(dis.readLine());
                    while (dis.available() > 0) {
                        buffer.append("\n").append(dis.readLine());
                    }
                }
                dis.close();
            } catch (final Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
            if (buffer != null) {
                out = buffer.toString();
            }
            return out;
        }

        public Process run(final String s) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(SHELL);
                final DataOutputStream toProcess = new DataOutputStream(
                        process.getOutputStream());
                toProcess.writeBytes("exec " + s + "\n");
                toProcess.flush();
            } catch (final Exception e) {
                Log.e(TAG, "Exception while trying to run: '" + s + "' "
                        + e.getMessage());
                process = null;
            }
            return process;
        }

        public CommandResult runWaitFor(final String s) {
            if (DEBUG) Log.d(TAG, "runWaitFor( " + s + " )");
            final Process process = run(s);
            Integer exit_value = null;
            String stdout = null;
            String stderr = null;
            if (process != null) {
                try {
                    exit_value = process.waitFor();
                    stdout = getStreamLines(process.getInputStream());
                    stderr = getStreamLines(process.getErrorStream());
                } catch (final InterruptedException e) {
                    Log.e(TAG, "runWaitFor " + e.toString());
                } catch (final NullPointerException e) {
                    Log.e(TAG, "runWaitFor " + e.toString());
                }
            }
            return new CommandResult(exit_value, stdout, stderr);
        }
    }

    public boolean canSU(final boolean force_check) {
        if (can_su == null || force_check) {
            final CommandResult r = su.runWaitFor("id");
            final StringBuilder out = new StringBuilder();
            if (r.stdout != null) {
                out.append(r.stdout).append(" ; ");
            }
            if (r.stderr != null) {
                out.append(r.stderr);
            }
            Log.d(TAG, "canSU() su[" + r.exit_value + "]: " + out);
            can_su = r.success();
        }
        return can_su;
    }
}
public class EasyPair<Ta, Tb> extends Pair<Ta, Tb> {
    /**
     * Constructor for a Pair. If either are null then equals() and hashCode() will throw
     * a NullPointerException.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public Ta first;
    public Tb second;
    public EasyPair(Ta first, Tb second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    /**
     * Constructs a new EasyPair from an existing EasyPair
     * @param pair
     */
    public EasyPair(EasyPair<Ta, Tb> pair) {
        super(pair.getFirst(), pair.getSecond());
        this.first = pair.getFirst();
        this.second = pair.getSecond();
    }

    @Override
    public String toString() {
        String first_;
        String second_;

        try {
            first_ = Arrays.toString((Ta[]) first);
        } catch (ClassCastException badCast) {
            first_ = first.toString();
        }

        try {
            second_ = Arrays.toString((Tb[]) second);
        } catch (ClassCastException badCast) {
            second_ = second.toString();
        }
        return "EasyPair<" + first_ + ", " + second_ + ">";
    }

    public Ta getFirst() {
        return first;
    }

    public Tb getSecond() {
        return second;
    }

    public void changeFirst(Ta newFirst) {
        this.first = newFirst;
    }

    public void changeSecond(Tb newSecond) {
        this.second = newSecond;
    }

    public EasyPair<Ta, Tb> clonePair() {
        return new EasyPair<Ta, Tb>(first, second);
    }

    public boolean equals() {
        // yea they equal but not in a good way
        if (first == null || second == null)
            return false;
        // return a method that won't throw null
        return first == second;
    }
}



}
