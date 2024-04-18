package com.mimosa.deeppokemon.refactor.crawler.eventhandler;

import com.mimosa.deeppokemon.refactor.entity.metadata.battle.BattleMetaData;
import com.mimosa.deeppokemon.refactor.exception.EventHandlerNotSupportException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

/**
 * 回合事件处理调度器测试
 *
 * @author huangxiaocong(2070132549@qq.com)
 */
@SpringBootTest
public class EventHandlerDispatcherTest {


    /**
     * 测试是否正确调度事件处理器
     *
     * @author huangxiaocong(2070132549@qq.com)
     */
    @Test
    public void dispatch() throws EventHandlerNotSupportException {
        String playerEvent = "|player|p1|Separation|sabrina|";
        BattleMetaData battleMetaData = new BattleMetaData();
        EventHandlerDispatcher handlerDispatcher = new EventHandlerDispatcher();

        // mock模拟事件处理器,验证是否执行过其handle方法
        PlayerEventStringHandler playerEventStringHandler = mock(PlayerEventStringHandler.class);
        doNothing().when(playerEventStringHandler).handle(isA(String.class), isA(BattleMetaData.class));

        handlerDispatcher.set(playerEventStringHandler);
        boolean isDispatch = handlerDispatcher.dispatch(playerEvent, battleMetaData);

        Assert.assertTrue(isDispatch);
        verify(playerEventStringHandler).handle(playerEvent, battleMetaData);
    }
}