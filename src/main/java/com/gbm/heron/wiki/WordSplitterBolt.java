package com.gbm.heron.wiki;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;


public class WordSplitterBolt extends BaseRichBolt {
	
	private static final long serialVersionUID = 5151173513759399636L;

	private final int minWordLength;

    private OutputCollector collector;

    public WordSplitterBolt(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        WikipediaEditEvent wEvent = (WikipediaEditEvent) input.getValueByField("data");


        //String lang = tweet.getLang();

        String text = wEvent.getSummary().replaceAll("\\p{Punct}", " ").replaceAll("\\r|\\n", "").toLowerCase();
        String[] words = text.split(" ");
        for (String word : words) {
            if (word.length() >= minWordLength) {
                collector.emit(new Values("tr", word));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("lang", "word"));
    }
}
