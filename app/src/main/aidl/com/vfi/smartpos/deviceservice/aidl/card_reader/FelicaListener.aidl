// FelicaHandler.aidl
package com.vfi.smartpos.deviceservice.aidl.card_reader;

import com.vfi.smartpos.deviceservice.aidl.card_reader.FelicaInfomation;
interface FelicaListener {

    /**
     * Felica search card result
     *
	 * @param ret 0-success 1-timeout -1-failed
	 * @param felicaInfos list of felica data;
     * @since 3.x.x
	 */
    void onSearchResult(int ret, in List<FelicaInfomation> felicaInfos);
}
