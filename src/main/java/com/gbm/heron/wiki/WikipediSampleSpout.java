/**
 * Taken from the storm-starter project on GitHub
 * https://github.com/nathanmarz/storm-starter/
 */
package com.gbm.heron.wiki;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings({"rawtypes", "serial"})
public class WikipediSampleSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(WikipediSampleSpout.class);
    /** Hostname of the server to connect to. */
    public static final String DEFAULT_HOST = "irc.wikimedia.org";

    /** Port of the server to connect to. */
    public static final int DEFAULT_PORT = 6667;

    public static final String DEFAULT_CHANNEL = "#en.wikipedia";

    private SpoutOutputCollector collector;
    private LinkedBlockingQueue<WikipediaEditEvent> queue;
    private WikipediaEditEventIrcStream wikipediaEditEventIrcStream;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        queue = new LinkedBlockingQueue<WikipediaEditEvent>(1000);
        this.collector = collector;
        wikipediaEditEventIrcStream = new WikipediaEditEventIrcStream(DEFAULT_HOST, DEFAULT_PORT);
        LOG.info("init wikipediaEditEventIrcStream");
        // Open connection and join channel
        try {
            wikipediaEditEventIrcStream.connect();
            LOG.info("wikipediaEditEventIrcStream connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
        wikipediaEditEventIrcStream.join(DEFAULT_CHANNEL);
        LOG.info("joined channel");
    }

    @Override
    public void nextTuple() {
        WikipediaEditEvent ret = wikipediaEditEventIrcStream.getEdits().poll();
        if (ret == null) {
            Utils.sleep(50);
        } else {
            collector.emit(new Values(ret));
        }
    }

    @Override
    public void close() {
        try {
            wikipediaEditEventIrcStream.close();
        } catch (Exception e) {

        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("data"));
    }
}
