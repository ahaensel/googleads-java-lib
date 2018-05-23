// Copyright 2018 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.api.ads.adwords.lib.selectorfields.v201802.cm;

import com.google.api.ads.adwords.lib.selectorfields.EntityField;
import com.google.api.ads.adwords.lib.selectorfields.Filterable;

/**
 * A {@code Enum} to facilitate the selection of fields for {@code OfflineDataUpload}.
 */
public enum OfflineDataUploadField implements EntityField {

  // Fields constants definitions

  /**
   * User specified upload id.
   */
  @Filterable
  ExternalUploadId(true),

  /**
   * Processing failure reason for get, if status is FAILURE.
   * Used for upload level failures.
   */
  @Filterable
  FailureReason(true),

  /**
   * Status of this upload.
   */
  @Filterable
  UploadStatus(true),

  ;

  private final boolean isFilterable;

  private OfflineDataUploadField(boolean isFilterable) {
    this.isFilterable = isFilterable;
  }

  @Override
  public boolean isFilterable() {
    return this.isFilterable;
  }

}