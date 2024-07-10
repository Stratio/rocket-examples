# rocket-distributed-deep-learning

This directory contains Dockerfiles that generates sample docker images extending Rocket Driver, Rocket Executor and Analytic Intelligence images, by adding and environment with [Horovod](https://github.com/horovod/horovod/tree/v0.28.1) library compiled with support for Apache Spark, Tensorflow, Keras and PyTorch. 

---
>**IMPORTANT NOTICE:**
>
>These images **are not intended** to be used in **production environments**. They are intended to be used as a reference for building your own images with the desired libraries and dependencies.
>
>The provided images has been successfully tested in an environment with nodes providing:
>* Nvidia Tesla T4 GPUs
>* Cuda 12.0
>* cuDNN 8.9.0
>* Nvidia Driver 520.61.05
>
>We do not guarantee that these images will work in environments with different GPU vendor or drivers version.
---

## Kyverno policies
In order to deploy Pods with these images, it is necessary to update the Kyverno policies of your namespaces (Rocket and Intelligence) by adding the new image names:

### Rocket
_restrict-rocket-images_:

                  containers:
                    - image: >-
                        */rocket-api:* | */rocket-driver* | */rocket-executor* |
                        */rocket-ml-prediction-server:* |
                        */rocket-mleap-microservice:* |
                        */rocket-mlflow-microservice:* |
                        */rocket-r-mlflow-microservice:* | */stratio-spark:*

### Intelligence
_restrict-intelligence-images_:

                  containers:
                    - image: >-
                        */intelligence-environment:* | */analytic-environment:*
                        | */analytic-environment-light:* |
                        */analytic-environment-horovod-gpu:* |
                        */intelligence-backup-restore:* | */stratio-spark:*

Add the pattern matching your image names.