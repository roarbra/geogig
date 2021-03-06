/* Copyright (c) 2016 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Gabriel Roldan (Boundless) - initial implementation
 */
package org.locationtech.geogig.storage.postgresql.v9;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.Rule;
import org.locationtech.geogig.storage.impl.ConflictsDatabaseConformanceTest;
import org.locationtech.geogig.storage.postgresql.config.Environment;
import org.locationtech.geogig.storage.postgresql.config.PGStorage;
import org.locationtech.geogig.storage.postgresql.config.PGTemporaryTestConfig;
import org.locationtech.geogig.storage.postgresql.config.PGTestDataSourceProvider;

public class PGConflictsDatabaseConformanceIT
        extends ConflictsDatabaseConformanceTest<PGConflictsDatabase> {

    public static @ClassRule PGTestDataSourceProvider ds = new PGTestDataSourceProvider();

    public @Rule PGTemporaryTestConfig testConfig = new PGTemporaryTestConfig(
            getClass().getSimpleName(), ds);

    protected @Override PGConflictsDatabase createConflictsDatabase() throws Exception {
        Environment env = testConfig.getEnvironment();
        PGStorage.createNewRepo(env);
        PGConflictsDatabase conflicts = new PGConflictsDatabase(env);
        conflicts.open();
        return conflicts;
    }

    protected @Override void dispose(@Nullable PGConflictsDatabase conflicts) throws Exception {
        if (conflicts != null) {
            conflicts.close();
        }
    }
}
