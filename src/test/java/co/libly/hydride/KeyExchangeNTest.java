/*
 * Copyright (c) Libly - Terl Tech Ltd  • 04/08/2019, 22:41 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.hydride;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyExchangeNTest extends BaseTest {

    private String context = "context1";
    private byte[] contextBytes = context.getBytes();

    private String message = "This is a message that will be encrypted.";
    private byte[] messageBytes = message.getBytes();

    @Test
    public void keyExchange() {
        Hydrogen.HydroKxKeyPair serverKeyPair = new Hydrogen.HydroKxKeyPair();
        hydrogen.hydro_kx_keygen(serverKeyPair);

        // Client: generate session keys and a packet with an ephemeral public key to send to the server
        Hydrogen.HydroKxSessionKeyPair clientSessionKp = new Hydrogen.HydroKxSessionKeyPair();
        byte[] packet1 = new byte[Hydrogen.HYDRO_KX_N_PACKET1BYTES];
        int genSuccess = hydrogen.hydro_kx_n_1(clientSessionKp, packet1, null, serverKeyPair.getPublicKey());
        assertEquals(0, genSuccess);

        // Send packet1 to the server...

        // Done! sessionKeyPair.tx is the key for sending data to the client,
        // and sessionKeyPair.rx is the key for receiving data from the client.
        // The session keys are the same as those computed by the client, but swapped.
        Hydrogen.HydroKxSessionKeyPair serverSessionKp = new Hydrogen.HydroKxSessionKeyPair();
        hydrogen.hydro_kx_n_2(serverSessionKp, packet1, null, serverKeyPair);

        assertTrue(encryptFromServerToClient(message, contextBytes, serverSessionKp.getTx(), clientSessionKp.getRx()));
    }


}
