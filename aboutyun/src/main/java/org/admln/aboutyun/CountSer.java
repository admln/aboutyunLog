package org.admln.aboutyun;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author admln
 *
 */
public class CountSer {
	
	/**  
	 * 计数器
	 * 用于计数各种异常数据
	 */ 
	enum Counter {
		TIMESKIP,     //时间格式有误
		IPSKIP,       //IP有误
		URLSKIP,      //URL有误
		SERSKIP,      //取得搜索词有误
	}
	/**
	 * 
	 * Map
	 *
	 */
	public static class countSerMapper extends Mapper<Object,Text,Text,IntWritable> {
		public final static IntWritable ONE = new IntWritable(1);
		public Text word = new Text();
		
		@Override
		public void setup(Context context) {
			
		}
		
		@Override
		public void map(Object key,Text value,Context context) throws IOException, InterruptedException{
			try {
				String serWord = Log.parser(value.toString()).getSerWord();
				if(serWord!=null) {
					word.set(serWord);
					context.write(word, ONE);
				}
			}catch(MyException eM) {
				context.getCounter(Counter.IPSKIP).increment(1);
			}
		}
		
		@Override
		public void cleanup(Context context) {
			
		}
	}
	
	/**
	 * 
	 * Reduce
	 */
	public static class countSerReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		
		int all = 0;
		int sum = 0;
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			
		}
		
		public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			Iterator<IntWritable> ite = values.iterator();
			sum = 0;
			while(ite.hasNext()) {
				sum++;
				all++;
				ite.next();
			}
			context.write(key, new IntWritable(sum));
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			context.write(new Text("总搜索量为:"), new IntWritable(all));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Path input = new Path("hdfs://192.168.32.131/in/aboutyunLog/");
		Path output = new Path("hdfs://192.168.32.131/out/aboutyunLogcountSer/");
		
		Configuration conf = new Configuration();
		
		Job job = new Job(conf,"countSer");
		
		job.setJarByClass(CountSer.class);
		
		job.setMapperClass(countSerMapper.class);
		job.setReducerClass(countSerReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		
		System.exit(job.waitForCompletion(true)?0:1);
	}

}
