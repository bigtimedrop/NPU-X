#include <jni.h>
#include <android/NeuralNetworks.h>
#include <vector>
#include <string>

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_npux_app_MainActivity_getNnapiDevices(JNIEnv *env, jobject /* this */) {

    // Localiza a data class NnapiDevice no pacote com.npux.app
    jclass deviceClass = env->FindClass("com/npux/app/NnapiDevice");
    if (!deviceClass) {
        return nullptr;
    }

    // Obtém o construtor: NnapiDevice(String, int, String, int)
    jmethodID constructor = env->GetMethodID(
        deviceClass,
        "<init>",
        "(Ljava/lang/String;ILjava/lang/String;I)V"
    );

    uint32_t numDevices = 0;
    // Obtém a quantidade de dispositivos de aceleração de IA do NNAPI
    if (ANeuralNetworks_getDeviceCount(&numDevices) != ANEURALNETWORKS_NO_ERROR || numDevices == 0) {
        return env->NewObjectArray(0, deviceClass, nullptr);
    }

    std::vector<jobject> deviceObjects;

    for (uint32_t i = 0; i < numDevices; ++i) {
        ANeuralNetworksDevice* device = nullptr;
        if (ANeuralNetworks_getDevice(i, &device) != ANEURALNETWORKS_NO_ERROR || !device) {
            continue;
        }

        const char* name = nullptr;
        int32_t type = 0;
        int64_t featureLevel = 0;
        const char* version = nullptr;

        // Extrai propriedades do dispositivo NNAPI
        ANeuralNetworksDevice_getName(device, &name);
        ANeuralNetworksDevice_getType(device, &type);

        // Feature level e version estão disponíveis na API level 29+ / 30+
        ANeuralNetworksDevice_getFeatureLevel(device, &featureLevel);
        ANeuralNetworksDevice_getVersion(device, &version);

        jstring jName = env->NewStringUTF(name ? name : "Unknown");
        jstring jVersion = env->NewStringUTF(version ? version : "N/A");

        // Cria a instância do Kotlin NnapiDevice
        jobject devObj = env->NewObject(
            deviceClass,
            constructor,
            jName,
            static_cast<jint>(type),
            jVersion,
            static_cast<jint>(featureLevel)
        );

        deviceObjects.push_back(devObj);

        // Limpa referências locais para evitar vazamento de memória JNI
        env->DeleteLocalRef(jName);
        env->DeleteLocalRef(jVersion);
    }

    // Cria o array Java/Kotlin NnapiDevice[] para retornar
    jobjectArray result = env->NewObjectArray(
        static_cast<jsize>(deviceObjects.size()),
        deviceClass,
        nullptr
    );

    for (size_t i = 0; i < deviceObjects.size(); ++i) {
        env->SetObjectArrayElement(result, static_cast<jsize>(i), deviceObjects[i]);
        env->DeleteLocalRef(deviceObjects[i]);
    }

    return result;
}
