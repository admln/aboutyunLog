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
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * @author admln
 *
 */
public class CountBlog {
	
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
	public static class countBlogMapper extends Mapper<Object,Text,Text,IntWritable> {
		public final static IntWritable ONE = new IntWritable(1);
		public Text word = new Text();
		
		@Override
		public void setup(Context context) {
			
		}
		
		@Override
		public void map(Object key,Text value,Context context) throws IOException, InterruptedException{
			try {
				Log log = Log.parser(value.toString());
				if(log.isBlog()) {
					word.set("1:"+log.getIp());
					context.write(word, ONE);
				}else if(log.isPersionalBlog()) {
					word.set("2:"+log.getIp());
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
	public static class countBlogReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		
		int sum = 0;
		private MultipleOutputs<Text,IntWritable> mos;
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			mos = new MultipleOutputs<Text, IntWritable>(context);
		}
		
		public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			Iterator<IntWritable> ite = values.iterator();
			sum = 0;
			while(ite.hasNext()) {
				sum++;
				ite.next();
			}
			if(key.toString().split(":")[0].equals("1")) {
				context.write(new Text(key.toString().split(":")[1]), new IntWritable(sum));
			}else {
				mos.write("test",new Text(key.toString().split(":")[1]), new IntWritable(sum));
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			mos.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Path input = new Path("hdfs://192.168.32.131/in/aboutyunLog/");
		Path output = new Path("hdfs://192.168.32.131/out/aboutyunLogcountBlog/");
		
		Configuration conf = new Configuration();
		
		Job job = new Job(conf,"countBlog");
		
		job.setJarByClass(CountBlog.class);
		
		job.setMapperClass(countBlogMapper.class);
		job.setReducerClass(countBlogReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		MultipleOutputs.addNamedOutput(job, "test", TextOutputFormat.class,Text.class, IntWritable.class);
		
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		
		System.exit(job.waitForCompletion(true)?0:1);
	}

}
