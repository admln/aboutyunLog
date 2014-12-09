package org.admln.aboutyun;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
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
public class CountIP {
	
	/**  
	 * 计数器
	 * 用于计数各种异常数据
	 */ 
	enum Counter {
		TIMESKIP,     //时间格式有误
		IPSKIP         //IP有误
	}
	/**
	 * 
	 * Map
	 *
	 */
	public static class countIPMapper extends Mapper<Object,Text,Text,IntWritable> {
		public final static IntWritable ONE = new IntWritable(1);
		public Text word = new Text();
		
		@Override
		public void setup(Context context) {
			
		}
		
		@Override
		public void map(Object key,Text value,Context context) throws IOException, InterruptedException{
			try {
				word.set(Log.parser(value.toString()).getIp());
				context.write(word, ONE);
			}catch(MyException e) {
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
	public static class countIPReducer extends Reducer<Text,IntWritable,NullWritable,Text> {
		
		Set<String> set = new HashSet<String>();
		
		@Override
		public void setup(Context context) {
			
		}
		
		public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			set.add(key.toString());
			context.write(NullWritable.get(), key);
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			context.write(NullWritable.get(), new Text("总数:" + String.valueOf(set.size())));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Path input = new Path("hdfs://192.168.32.131/in/aboutyunLog/");
		Path output = new Path("hdfs://192.168.32.131/out/aboutyunLogCountIP/");
		
		Configuration conf = new Configuration();
		
		Job job = new Job(conf,"countIP");
		
		job.setJarByClass(CountIP.class);
		
		job.setMapperClass(countIPMapper.class);
		job.setReducerClass(countIPReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		
		System.exit(job.waitForCompletion(true)?0:1);
	}

}
