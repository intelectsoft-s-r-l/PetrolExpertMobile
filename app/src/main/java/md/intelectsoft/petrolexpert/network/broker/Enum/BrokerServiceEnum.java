package md.intelectsoft.petrolexpert.network.broker.Enum;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Tony on 2017/12/3.
 */

public class BrokerServiceEnum {
    public static final int Retail = 10, SalesAgent = 20, StockManager = 21, Restaurant = 30, WaiterAssistant = 31, KitchenAssistant = 32, EDX = 40, SMSService = 50, MyDiscount = 60, MyQuickMenu = 70, MyShop = 80, MyCarWash = 90, QIWI = 100, IQOS = 110, CashPetrolExpert = 130;
    public static final int Windows = 1, Android = 2, iOS = 3, Linux = 4;

    @IntDef({Retail, SalesAgent, StockManager, Restaurant, WaiterAssistant, KitchenAssistant, EDX, SMSService, MyDiscount, MyQuickMenu, MyShop, MyCarWash, QIWI, IQOS, CashPetrolExpert})
    @Retention(RetentionPolicy.SOURCE)
    public @interface productType {
    }

    @IntDef({Windows, Android, iOS, Linux})
    @Retention(RetentionPolicy.SOURCE)
    public @interface osType {
    }
}
