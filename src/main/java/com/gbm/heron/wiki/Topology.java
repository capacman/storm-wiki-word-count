package com.gbm.heron.wiki;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.NotAliveException;
import backtype.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Topology {

	static final String TOPOLOGY_NAME = "storm-wiki-word-count";
	static final Logger LOG  = LoggerFactory.getLogger(Topology.class);

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		Config config = new Config();
		config.setMessageTimeoutSecs(120);
        /*config.put("consumerKey","8XvhA4kui4791sIvqGIAsUoDC");
        config.put("consumerSecret","3emLFQzgl5U9NL7J3ZNIsAbHo0QcKm1wBATAsKG7dccL8sJv8D");
        config.put("accessToken","231931663-p403jrcOPuFeMenjPs7I5F2OveyD0v4owEi6YHE3");
        config.put("accessTokenSecret","ITXN1fwKSVdBeN01NZRdz4uFkKwSHYLyR5Au5Hp6DyqXL");*/
        config.registerSerialization(WikipediaEditEvent.class);

		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("WikipediSampleSpout", new WikipediSampleSpout());
        b.setBolt("WordSplitterBolt", new WordSplitterBolt(5)).shuffleGrouping("WikipediSampleSpout");
        b.setBolt("IgnoreWordsBolt", new IgnoreWordsBolt()).shuffleGrouping("WordSplitterBolt");
        b.setBolt("WordCounterBolt", new WordCounterBolt(10, 5 * 60, 50)).shuffleGrouping("IgnoreWordsBolt");

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					cluster.killTopology(TOPOLOGY_NAME);
					cluster.shutdown();
				} catch (NotAliveException e) {
					LOG.error("not elive",e);
				}

			}
		});

	}

}
