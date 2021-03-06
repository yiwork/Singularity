package com.hubspot.deploy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hubspot.mesos.JavaUtils;
import com.hubspot.singularity.executor.SingularityExecutorLogrotateFrequency;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Custom executor settings")
public class ExecutorData {

  private final String cmd;
  private final List<EmbeddedArtifact> embeddedArtifacts;
  private final List<ExternalArtifact> externalArtifacts;
  private final List<S3Artifact> s3Artifacts;
  private final List<Integer> successfulExitCodes;
  private final Optional<String> runningSentinel;
  private final Optional<String> user;
  private final List<String> extraCmdLineArgs;
  private final Optional<String> loggingTag;
  private final Map<String, String> loggingExtraFields;
  private final Optional<Long> sigKillProcessesAfterMillis;
  private final Optional<Integer> maxTaskThreads;
  private final Optional<Boolean> preserveTaskSandboxAfterFinish;
  private final Optional<Integer> maxOpenFiles;
  private final Optional<Boolean> skipLogrotateAndCompress;
  private final Optional<List<S3ArtifactSignature>> s3ArtifactSignatures;
  private final Optional<SingularityExecutorLogrotateFrequency> logrotateFrequency;

  @JsonCreator
  public ExecutorData(@JsonProperty("cmd") String cmd,
                      @JsonProperty("embeddedArtifacts") List<EmbeddedArtifact> embeddedArtifacts,
                      @JsonProperty("externalArtifacts") List<ExternalArtifact> externalArtifacts,
                      @JsonProperty("s3Artifacts") List<S3Artifact> s3Artifacts,
                      @JsonProperty("successfulExitCodes") List<Integer> successfulExitCodes,
                      @JsonProperty("user") Optional<String> user,
                      @JsonProperty("runningSentinel") Optional<String> runningSentinel,
                      @JsonProperty("extraCmdLineArgs") List<String> extraCmdLineArgs,
                      @JsonProperty("loggingTag") Optional<String> loggingTag,
                      @JsonProperty("loggingExtraFields") Map<String, String> loggingExtraFields,
                      @JsonProperty("sigKillProcessesAfterMillis") Optional<Long> sigKillProcessesAfterMillis,
                      @JsonProperty("maxTaskThreads") Optional<Integer> maxTaskThreads,
                      @JsonProperty("preserveTaskSandboxAfterFinish") Optional<Boolean> preserveTaskSandboxAfterFinish,
                      @JsonProperty("maxOpenFiles") Optional<Integer> maxOpenFiles,
                      @JsonProperty("skipLogrotateAndCompress") Optional<Boolean> skipLogrotateAndCompress,
                      @JsonProperty("s3ArtifactSignatures") Optional<List<S3ArtifactSignature>> s3ArtifactSignatures,
                      @JsonProperty("logrotateFrequency") Optional<SingularityExecutorLogrotateFrequency> logrotateFrequency) {
    this.cmd = cmd;
    this.embeddedArtifacts = JavaUtils.nonNullImmutable(embeddedArtifacts);
    this.externalArtifacts = JavaUtils.nonNullImmutable(externalArtifacts);
    this.s3Artifacts = JavaUtils.nonNullImmutable(s3Artifacts);
    this.user = user;
    this.successfulExitCodes = JavaUtils.nonNullImmutable(successfulExitCodes);
    this.extraCmdLineArgs = JavaUtils.nonNullImmutable(extraCmdLineArgs);
    this.runningSentinel = runningSentinel;
    this.loggingTag = loggingTag;
    this.loggingExtraFields = JavaUtils.nonNullImmutable(loggingExtraFields);
    this.sigKillProcessesAfterMillis = sigKillProcessesAfterMillis;
    this.maxTaskThreads = maxTaskThreads;
    this.preserveTaskSandboxAfterFinish = preserveTaskSandboxAfterFinish;
    this.maxOpenFiles = maxOpenFiles;
    this.skipLogrotateAndCompress = skipLogrotateAndCompress;
    this.s3ArtifactSignatures = s3ArtifactSignatures;
    this.logrotateFrequency = logrotateFrequency;
  }

  public ExecutorDataBuilder toBuilder() {
    return new ExecutorDataBuilder(cmd, embeddedArtifacts, externalArtifacts, s3Artifacts, successfulExitCodes, runningSentinel, user, extraCmdLineArgs, loggingTag,
        loggingExtraFields, sigKillProcessesAfterMillis, maxTaskThreads, preserveTaskSandboxAfterFinish, maxOpenFiles, skipLogrotateAndCompress, s3ArtifactSignatures,
        logrotateFrequency);
  }

  @Schema(required = true, description = "Command for the custom executor to run")
  public String getCmd() {
    return cmd;
  }

  @Schema()
  public Optional<String> getLoggingTag() {
    return loggingTag;
  }

  @Schema()
  public Map<String, String> getLoggingExtraFields() {
    return loggingExtraFields;
  }

  @Schema(description = "A list of the full content of any embedded artifacts")
  public List<EmbeddedArtifact> getEmbeddedArtifacts() {
    return embeddedArtifacts;
  }

  @Schema(description = "A list of external artifacts for the executor to download")
  public List<ExternalArtifact> getExternalArtifacts() {
    return externalArtifacts;
  }

  @Schema(description = "Allowable exit codes for the task to be considered FINISHED instead of FAILED")
  public List<Integer> getSuccessfulExitCodes() {
    return successfulExitCodes;
  }

  @Schema(description = "Extra arguments in addition to any provided in the cmd field")
  public List<String> getExtraCmdLineArgs() {
    return extraCmdLineArgs;
  }

  @Schema()
  public Optional<String> getRunningSentinel() {
    return runningSentinel;
  }

  @Schema(description = "Run the task process as this user")
  public Optional<String> getUser() {
    return user;
  }

  @Schema(description = "Send a sigkill to a process if it has not shut down this many millis after being sent a term signal")
  public Optional<Long> getSigKillProcessesAfterMillis() {
    return sigKillProcessesAfterMillis;
  }

  @Schema(description = "List of s3 artifacts for the executor to download")
  public List<S3Artifact> getS3Artifacts() {
    return s3Artifacts;
  }

  @Schema(description = "Maximum number of threads a task is allowed to use")
  public Optional<Integer> getMaxTaskThreads() {
    return maxTaskThreads;
  }

  @Schema(description = "If true, do not delete files in the task sandbox after the task process has terminated")
  public Optional<Boolean> getPreserveTaskSandboxAfterFinish() {
    return preserveTaskSandboxAfterFinish;
  }

  @Schema(description = "Maximum number of open files the task process is allowed")
  public Optional<Integer> getMaxOpenFiles() {
    return maxOpenFiles;
  }

  @Schema(description = "If true, do not run logrotate or compress old log files")
  public Optional<Boolean> getSkipLogrotateAndCompress() {
    return skipLogrotateAndCompress;
  }

  @Schema(description = "A list of signatures use to verify downloaded s3artifacts")
  public Optional<List<S3ArtifactSignature>> getS3ArtifactSignatures() {
    return s3ArtifactSignatures;
  }

  @JsonIgnore
  public List<S3ArtifactSignature> getS3ArtifactSignaturesOrEmpty() {
    return s3ArtifactSignatures.orElse(Collections.<S3ArtifactSignature> emptyList());
  }

  @Schema(description = "Run logrotate this often. Can be HOURLY, DAILY, WEEKLY, MONTHLY")
  public Optional<SingularityExecutorLogrotateFrequency> getLogrotateFrequency() {
    return logrotateFrequency;
  }

  @Override
  public String toString() {
    return "ExecutorData{" +
        "cmd='" + cmd + '\'' +
        ", embeddedArtifacts=" + embeddedArtifacts +
        ", externalArtifacts=" + externalArtifacts +
        ", s3Artifacts=" + s3Artifacts +
        ", successfulExitCodes=" + successfulExitCodes +
        ", runningSentinel=" + runningSentinel +
        ", user=" + user +
        ", extraCmdLineArgs=" + extraCmdLineArgs +
        ", loggingTag=" + loggingTag +
        ", loggingExtraFields=" + loggingExtraFields +
        ", sigKillProcessesAfterMillis=" + sigKillProcessesAfterMillis +
        ", maxTaskThreads=" + maxTaskThreads +
        ", preserveTaskSandboxAfterFinish=" + preserveTaskSandboxAfterFinish +
        ", maxOpenFiles=" + maxOpenFiles +
        ", skipLogrotateAndCompress=" + skipLogrotateAndCompress +
        ", s3ArtifactSignatures=" + s3ArtifactSignatures +
        ", logrotateFrequency=" + logrotateFrequency +
        '}';
  }
}
