package com.ouyangliuy.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class Main02 {
    public static void main(String[] args) throws Exception {
        //the factory for the event
        LongEventFactory factory = new LongEventFactory();

        //Specify the of the ring buffer,must be power of 2.
        int bufferSize = 1024;

        //Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);

        //Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        //Start the Disruptor,start all threads running
        disruptor.start();

        //Get the ring buffer form the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //========================================================================
        EventTranslator<LongEvent> translator1 = new EventTranslator<LongEvent>() {
            @Override
            public void translateTo(LongEvent event, long sequence) {
                event.set(8888L);
            }
        };
        ringBuffer.publishEvent(translator1);
        //========================================================================
        EventTranslatorOneArg<LongEvent, Long> translator2 = new EventTranslatorOneArg<LongEvent, Long>() {
            @Override
            public void translateTo(LongEvent event, long sequence, Long l) {
                event.set(l);
            }
        };
        ringBuffer.publishEvent(translator2, 7777L);
        //========================================================================
        EventTranslatorTwoArg<LongEvent, Long, Long> translator3 = new EventTranslatorTwoArg<LongEvent, Long, Long>() {
            @Override
            public void translateTo(LongEvent event, long sequence, Long l1, Long l2) {
                event.set(l1);
            }
        };
        ringBuffer.publishEvent(translator3, 10000L, 10000L);
        //========================================================================
        EventTranslatorThreeArg<LongEvent, Long, Long, Long> translator4 = new EventTranslatorThreeArg<LongEvent, Long, Long, Long>() {
            @Override
            public void translateTo(LongEvent event, long sequence, Long l1, Long l2, Long l3) {
                event.set(l1 + l2 + l3);
            }
        };
        ringBuffer.publishEvent(translator4, 10000L, 10000L, 10000L);
        //========================================================================
      /*  EventTranslatorVararg<LongEvent> translator5 = new EventTranslatorThreeArg<LongEvent>() {
            @Override
            public void translateTo(LongEvent event, long l, Object o2, Object o3, Object o4) {
                long result = 0;
                long l = (Long) o2;
                result += l;

            }
        };
        ringBuffer.publishEvent(translator5, 10000L, 10000L, 10000L, 10000L);*/
    }
}