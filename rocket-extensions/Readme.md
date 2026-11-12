# Rocket extensions

## UDF

// TODO

## Plugins

- Legacy versions (Documentation in branches older than 9.5 links it)  
    - /old-extensions/input-lite-xd  
    - /old-extensions/output-lite-xd  
    - /old-extensions/transformation-lite-xd

**Note: plugins generated with new SDK versions must be included in new folders:**

- rocket-1.0.0-SDK  
    - Custom Input: GeneratorXDLiteInputStepBatch & GeneratorXDLiteInputStepStreaming
    - Custom Transform: RepartitionXDLiteTransformStepBatch & RepartitionXDLiteTransformStepStreaming
    - Custom Transform: TokenizerTransformStepBatch & TokenizerTransformStepStreaming
    - Custom Output: LoggerXDLiteOutputStep

- rocket-1.1.0-SDK      
    - New functionality: metadata management 
    - New step: MetadataTestXDLiteInputStepBatch

- rocket-2.2.0-SDK  
    - New functionality: Execution report logs in custom steps
    - New steps: ReportLogTestXDLiteInputStepBatch & ReportLogTestXDLiteInputStepStreaming 
    - Added reporting in:  
        - GeneratorXDLiteInputStepBatch & GeneratorXDLiteInputStepStreaming
        - TokenizerTransformStepBatch & TokenizerTransformStepStreaming
        - LoggerXDLiteOutputStep

- rocket-2.3.0-SDK  
    - New functionality: Lineage and QRs definition in custom steps
    - New steps: JdbcWithLineageXDLiteOutputStep

- rocket-3.0.0-SDK  
    - New functionality: Hybrid custom steps

- rocket-4.2.0-SDK  
    - Same steps as rocket-3.0.0-SDK built for Spark 4.2.0, Scala 2.13 and Java 17
    - Lineage and QRs: `sscc-rocket` 1.2.0-M1 and Pekko instead of `sscc-commons` and Akka
