// Copyright 2016 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package dfp.axis.v201711.packageservice;

import static com.google.api.ads.common.lib.utils.Builder.DEFAULT_CONFIGURATION_FILENAME;

import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201711.StatementBuilder;
import com.google.api.ads.dfp.axis.v201711.ApiError;
import com.google.api.ads.dfp.axis.v201711.ApiException;
import com.google.api.ads.dfp.axis.v201711.Package;
import com.google.api.ads.dfp.axis.v201711.PackagePage;
import com.google.api.ads.dfp.axis.v201711.PackageServiceInterface;
import com.google.api.ads.dfp.axis.v201711.PackageStatus;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.auth.oauth2.Credential;
import java.rmi.RemoteException;

/**
 * This example gets all packages in progress.
 *
 * <p>Credentials and properties in {@code fromFile()} are pulled from the
 * "ads.properties" file. See README for more info.
 */
public class GetInProgressPackages {

  /**
   * Runs the example.
   *
   * @param dfpServices the services factory.
   * @param session the session.
   * @throws ApiException if the API request failed with one or more service errors.
   * @throws RemoteException if the API request failed due to other errors.
   */
  public static void runExample(DfpServices dfpServices, DfpSession session)
      throws RemoteException {
    PackageServiceInterface packageService =
        dfpServices.get(session, PackageServiceInterface.class);

    // Create a statement to select packages.
    StatementBuilder statementBuilder = new StatementBuilder()
        .where("status = :status")
        .orderBy("id ASC")
        .limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
        .withBindVariableValue("status", PackageStatus.IN_PROGRESS.toString());

    // Retrieve a small amount of packages at a time, paging through
    // until all packages have been retrieved.
    int totalResultSetSize = 0;
    do {
      PackagePage page =
          packageService.getPackagesByStatement(statementBuilder.toStatement());

      if (page.getResults() != null) {
        // Print out some information for each package.
        totalResultSetSize = page.getTotalResultSetSize();
        int i = page.getStartIndex();
        for (Package pkg : page.getResults()) {
          System.out.printf(
              "%d) Package with ID %d, name '%s', and proposal ID %d was found.%n",
              i++,
              pkg.getId(),
              pkg.getName(),
              pkg.getProposalId()
          );
        }
      }

      statementBuilder.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
    } while (statementBuilder.getOffset() < totalResultSetSize);

    System.out.printf("Number of results found: %d%n", totalResultSetSize);
  }

  public static void main(String[] args) {
    DfpSession session;
    try {
      // Generate a refreshable OAuth2 credential.
      Credential oAuth2Credential =
          new OfflineCredentials.Builder().forApi(Api.DFP).fromFile().build().generateCredential();

      // Construct a DfpSession.
      session = new DfpSession.Builder().fromFile().withOAuth2Credential(oAuth2Credential).build();
    } catch (ConfigurationLoadException cle) {
      System.err.printf(
          "Failed to load configuration from the %s file. Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, cle);
      return;
    } catch (ValidationException ve) {
      System.err.printf(
          "Invalid configuration in the %s file. Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, ve);
      return;
    } catch (OAuthException oe) {
      System.err.printf(
          "Failed to create OAuth credentials. Check OAuth settings in the %s file. "
              + "Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, oe);
      return;
    }

    DfpServices dfpServices = new DfpServices();

    try {
      runExample(dfpServices, session);
    } catch (ApiException apiException) {
      // ApiException is the base class for most exceptions thrown by an API request. Instances
      // of this exception have a message and a collection of ApiErrors that indicate the
      // type and underlying cause of the exception. Every exception object in the dfp.axis
      // packages will return a meaningful value from toString
      //
      // ApiException extends RemoteException, so this catch block must appear before the
      // catch block for RemoteException.
      System.err.println("Request failed due to ApiException. Underlying ApiErrors:");
      if (apiException.getErrors() != null) {
        int i = 0;
        for (ApiError apiError : apiException.getErrors()) {
          System.err.printf("  Error %d: %s%n", i++, apiError);
        }
      }
    } catch (RemoteException re) {
      System.err.printf("Request failed unexpectedly due to RemoteException: %s%n", re);
    }
  }
}